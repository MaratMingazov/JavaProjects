package plugin2.views;

public class VisualizationUtils {
	private static String htmlHeader = "<html><head><title>Stack</title><style type=\"text/css\">body{background-color: white;}*{font-family: monospace;}div.ar{background-color: #FDF1FA; padding: 6px; margin-bottom: 12px; border: 1px solid #bbb;}div.ar_title{font-size: small; color: #669999;}.ar_info, .ar_info td{border: 1px solid #FDF1FA; border-collapse: collapse; padding: 4px;}.ar_vars, .ar_vars td{border: 1px solid #ccc; border-collapse: collapse; padding: 6px;}.ar_info .n, .ar_vars .title td{font-size: 10pt; color: #669999;}.ar_info{font-size: small; border-color: #FDF1FA;}.gr{color: grey; font-size: 8pt;} td.arg { background-color: #d9ffb3; }</style></head><body>";
	private static String htmlFooter = "</body></html>";

	// Template's params list: function, file, start address, args, rows, end
	// address
	private static String activationRecordTemplate = "<div class=\"ar\"><div class=\"ar_title\">Activation Record</div><table class=\"ar_info\"> <tr> <td class=\"n\">Function</td><td class=\"v\">%s</td></tr><tr> <td class=\"n\">File</td><td class=\"v\">%s</td></tr><tr> <td colspan=\"2\"> <table class=\"ar_vars\"> <thead> <tr class=\"title\"> <td>Address</td><td>Type</td><td>Value</td><td>Name</td></tr></thead> <tbody> <tr> <td>%s</td><td colspan=\"3\" class=\"gr\">start address</td></tr>%s %s<tr> <td>%s</td><td colspan=\"3\" class=\"gr\">end address</td></tr></tbody> </table> </td></tr></table></div>";

	// Template's params list: addr, type, value, name
	private static String varsRowTemplate = "<tr> <td class=\"c_addr\">%s</td><td class=\"c_type\">%s</td><td class=\"c_value\">%s</td><td class=\"c_name\">%s</td></tr>";
	private static String argsRowTemplate = "<tr> <td class=\"c_addr arg\">%s</td><td class=\"c_type arg\">%s</td><td class=\"c_value arg\">%s</td><td class=\"c_name arg\">%s</td></tr>";

	public static String composeStackTab(ActivationRecord[] frames) {
		StringBuilder html = new StringBuilder();

		html.append(htmlHeader);
		if (frames != null) {
			for (ActivationRecord frame : frames) {

				StringBuilder args = new StringBuilder();

				if (frame.getArgs() != null)
					for (VarDescription v : frame.getArgs()) {
						String row = String.format(argsRowTemplate, v.getAddress(), v.getType(), v.getValue(),
								v.getName());
						args.append(row);
					}

				StringBuilder rows = new StringBuilder();

				if (frame.getVars() != null)
					for (VarDescription v : frame.getVars()) {
						String row = String.format(varsRowTemplate, v.getAddress(), v.getType(), v.getValue(),
								v.getName());
						rows.append(row);
					}

				String activationRecord = String.format(activationRecordTemplate, frame.getFunctionName(),
						frame.getFileName(), frame.getStartAddress(), args.toString(), rows.toString(),
						frame.getEndAddress());

				html.append(activationRecord);
			}
		}

		html.append(htmlFooter);

		return html.toString();
	}
}
