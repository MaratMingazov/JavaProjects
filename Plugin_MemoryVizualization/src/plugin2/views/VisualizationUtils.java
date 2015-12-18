package plugin2.views;


import java.util.Arrays;

public class VisualizationUtils {
	private static String htmlHeader = "<html><head><title>Stack</title><style type=\"text/css\">body{background-color: white;}*{font-family: monospace; font-size:10pt;}div.ar{background-color: #FDF1FA; padding: 6px; margin-bottom: 12px; border: 1px solid #bbb;}div.ar_title{font-size: small; color: #669999;}.ar_info, .ar_info td{border: 1px solid #FDF1FA; border-collapse: collapse; padding: 4px;}.ar_vars, .ar_vars td{border: 1px solid #ccc; border-collapse: collapse; padding: 6px;}.ar_info .n, .ar_vars .title td{font-size: 10pt; color: #669999;}.ar_info{font-size: small; border-color: #FDF1FA;}.gr{color: grey; font-size: 8pt;} td.arg { background-color: #d9ffb3; } .collapsibleList li > input + *{display: none;}.collapsibleList li > input:checked + *{display: block;}.collapsibleList{list-style-type: none;}.collapsibleList li > input{display: none;}.collapsibleList label{cursor: pointer; text-decoration: underline;}.fixed{position: fixed; top: 0; left: 6;}</style></head><body>";
	private static String htmlFooter = "</body><script>window.onload=function(){var avatarElem=document.getElementById('ff'); var avatarSourceBottom=avatarElem.getBoundingClientRect().bottom + window.pageYOffset; window.onscroll=function(){if (avatarElem.classList.contains('fixed') && window.pageYOffset < avatarSourceBottom){avatarElem.classList.remove('fixed');}else if (window.pageYOffset > avatarSourceBottom){avatarElem.classList.add('fixed');}};};</script></html>";

	// Template's params list: function, file, line, end address, args, rows, start address
	// address, return value type, return value
	private static String activationRecordTemplate = "<div class=\"ar collapsibleList\"><div class=\"ar_title\">Activation Record</div><table class=\"ar_info\"> <tr> <td class=\"n\">Function</td><td class=\"v\">%s</td></tr><tr> <td class=\"n\">File</td><td class=\"v\">%s</td></tr><tr> <td class=\"n\">Line</td><td class=\"v\">%s</td></tr><tr> <td colspan=\"2\"> <table class=\"ar_vars\"> <thead> <tr class=\"title\"> <td>Address</td><td>Type</td><td>Value</td><td>Name</td></tr></thead> <tbody> <tr> <td>%s</td><td colspan=\"3\" class=\"gr\">end address</td></tr>%s</tbody> </table> </td></tr></table></div>";

	// Template's params list: addr, type, value, name
	private static String varsRowTemplate = "<tr> <td class=\"c_addr\">%s</td><td class=\"c_type\">%s</td><td class=\"c_value\">%s</td><td class=\"c_name\">%s</td></tr>";
	private static String argsRowTemplate = "<tr> <td class=\"c_addr arg\">%s</td><td class=\"c_type arg\">%s</td><td class=\"c_value arg\">%s</td><td class=\"c_name arg\">%s</td></tr>";

	// Template's params list: addr, type, UNIQUE ID ,value, name
	private static String varsRowWithNestedTemplate = "<tr> <td class=\"c_addr\">%s</td><td class=\"c_type\">%s</td><td class=\"c_value\"><label for=\"mln%s\">%s</label></td><td class=\"c_name\">%s</td></tr>";
	
	private static String varsTableHeader = "<table class=\"ar_vars\"> <thead> <tr class=\"title\"> <td>Address</td><td>Type</td><td>Value</td><td>Name</td></tr></thead> <tbody>";
	private static String varsTableFooter = "</tbody></table>";
	
	private static int idCounter;
	
	{
		idCounter = 0;
	}

	private static String getUniqueId(boolean nextPlease) {
		if (nextPlease) {
			idCounter++;
		}
		return Integer.toString(idCounter);
	}
	
	public static String composeStackTab(ActivationRecord[] frames, String eaxType, String eaxValue) {
		StringBuilder html = new StringBuilder();

		html.append(htmlHeader);
		html.append(String.format("<div class=\"ar\" id=\"ff\">EAX: (%s) %s</div>", eaxType, eaxValue));
		if (frames != null) {
			for (ActivationRecord frame : frames) {

				StringBuilder args = new StringBuilder();

				String varsTable = String.format(composeVarsTable(frame.getArgs(), frame.getVars(), true), frame.getStartAddress());

				String activationRecord = String.format(activationRecordTemplate, frame.getFunctionName(),
						frame.getFileName(), frame.getLineNumber(), frame.getEndAddress(), varsTable);

				html.append(activationRecord);
			}
		}

		html.append(htmlFooter);

		return html.toString();
	}
	
	private static String composeVarsTable(VarDescription[] args, VarDescription[] vars, boolean firstTime) {
		StringBuilder builder = new StringBuilder();
		
		if (!firstTime) {
			builder.append(varsTableHeader);
		}
		
		

		VarDescription[] nested;
		if (vars != null) {
			Arrays.sort(vars);
			for (VarDescription var : vars) {
				if ((nested = var.getNested()) != null && nested.length > 0) {
					builder.append(String.format(varsRowWithNestedTemplate, var.getAddress(), var.getType(),
							getUniqueId(true), var.getValue(), var.getName()));
					builder.append(String.format("<tr><td colspan=\"4\"><li><input type=\"checkbox\" id=\"mln%s\" /><div>", getUniqueId(false)));
					
					builder.append(composeVarsTable(null, nested, false));
					
					builder.append("</div></li></td></tr>");
				} else {
					builder.append(String.format(varsRowTemplate, var.getAddress(), var.getType(), var.getValue(),
							var.getName()));
				}
			}
		}
		
		if (firstTime) {
			builder.append("<tr><td>%s</td><td colspan=\"3\" class=\"gr\">start address</td></tr>");
		}
		
		if (args != null) {
			Arrays.sort(args);
			for (VarDescription arg : args) {
				builder.append(
						String.format(argsRowTemplate, arg.getAddress(), arg.getType(), arg.getValue(), arg.getName()));
			}
		}

		if (!firstTime) {
			builder.append(varsTableFooter);
		}
		return builder.toString();
	}
}
