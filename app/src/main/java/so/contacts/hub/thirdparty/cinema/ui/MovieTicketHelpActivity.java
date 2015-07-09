package so.contacts.hub.thirdparty.cinema.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.yulong.android.contacts.discover.R;

public class MovieTicketHelpActivity extends Activity {
	
	private final static String TICKET_HELP_RESULT="TICKET_HELP";
	
	private String tickHelpContent;

	private WebView tickethelp_content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		parseIntent();
		setContentView(R.layout.putao_layout_movie_tickethelp_content);
		tickethelp_content = (WebView) findViewById(R.id.webview_tickethelp_content);
		tickethelp_content.loadDataWithBaseURL(null, tickHelpContent, "text/html", "utf-8", null);
	}

	private void parseIntent() {
		// TODO Auto-generated method stub
		tickHelpContent=getIntent().getStringExtra(TICKET_HELP_RESULT);
	}
}
