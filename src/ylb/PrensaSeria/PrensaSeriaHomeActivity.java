package ylb.PrensaSeria;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Element;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;

public class PrensaSeriaHomeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String xmlRss = new Llamador().execute("").toString();       
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;		
		Document doc = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();			
			doc = dBuilder.parse(new ByteArrayInputStream(xmlRss.getBytes()));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		//doc.getDocumentElement().normalize();
		TreeMap<String, String> xmlMapping = null;
		NodeList nList = doc.getElementsByTagName("item");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			 
			   Node nNode = nList.item(temp);
			   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
			      Element eElement = (Element) nNode;
			      xmlMapping.put(getTagValue("title", eElement),getTagValue("description", eElement));
			      /*
			      System.out.println("First Name : " + getTagValue("firstname", eElement));
			      System.out.println("Last Name : " + getTagValue("lastname", eElement));
		              System.out.println("Nick Name : " + getTagValue("nickname", eElement));
			      System.out.println("Salary : " + getTagValue("salary", eElement));
	 				*/
			   }
			}
       
        
        ArrayList<String> lista = new ArrayList<String>();
        for(Map.Entry<String, String> entry: xmlMapping.entrySet()){
        	lista.add(entry.getKey());
        }
        final GridView grid = (GridView)findViewById(R.id.grid);
        ArrayAdapter ad = new ArrayAdapter<String>(this.getApplicationContext(),R.id.grid,lista);
        grid.setAdapter(ad);
    }
    private static String getTagValue(String sTag, Element eElement) {
    	NodeList nlList = ((Document) eElement).getElementsByTagName(sTag).item(0).getChildNodes();
     
            Node nValue = (Node) nlList.item(0);
     
    	return nValue.getNodeValue();
      }
}
class Llamador extends AsyncTask<String, Void, String>{
	@Override
	protected String doInBackground(String... params) {
		HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://www.elcorreo.com/vizcaya/rss/feeds/ultima.xml");
        HttpResponse response = null;
        try{
        	response=httpClient.execute(httpGet);
        } catch (ClientProtocolException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        InputStream inputStream = null;
        String result = null;
        try {
			inputStream = entity.getContent();
			result= convertStreamToString(inputStream);
			inputStream.close();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return result;
	}
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));		
		StringBuilder sb = new StringBuilder();		
		String line = null;		
		try {		
			while ((line = reader.readLine()) != null) {		
				sb.append(line + "\n");		
			}		
		} catch (IOException e) {	
			e.printStackTrace();		
		} finally {		
			try {		
				is.close();	
			} catch (IOException e) {		
				e.printStackTrace();		
			}		
		}		
		return sb.toString();		
	}
}