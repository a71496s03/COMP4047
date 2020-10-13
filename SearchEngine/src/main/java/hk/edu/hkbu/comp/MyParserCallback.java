package hk.edu.hkbu.comp;

import javax.swing.text.html.*;

class MyParserCallback extends HTMLEditorKit.ParserCallback {
	public String content = new String();

	@Override
	public void handleText(char[] data, int pos) {
		content += " " + new String(data);
	}
}
