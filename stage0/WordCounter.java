import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class WordCounter {
	private String urlStr;
    private String content;
    
    public WordCounter(String urlStr){
    	this.urlStr = urlStr;
    }
    
    private String fetchContent() throws IOException{
		URL url = new URL(this.urlStr);
		URLConnection conn = url.openConnection();
		InputStream in = conn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
	
		String retVal = "";
	
		String line = null;
		
		while ((line = br.readLine()) != null){
		    retVal = retVal + line + "\n";
		}
	
		return retVal;
    }
    
    public int BoyerMoore(String T, String P, int start){
        int n = T.length();
        int m = P.length();
        
        if(m == 0){
            return -1;
        }

        int[] L = last(P);

        int i = start + m - 1;
        int j = m - 1;

        while(i <= n -1 ){
            if(T.charAt(i) == P.charAt(j)){
                if(j == 0){
                    return i;
                }
                i--;
                j--;
            }
            else{
                int l = L[T.charAt(i)];
                i += m - Math.min(j, l+1);
                j = m-1;
            }
        }

        return -1;
    }

    public int[] last(String P){
    	// Bonus: Implement last occurence function
    	int [] L = new int[Character.MAX_VALUE + 1];

        Arrays.fill(L, -1);

        for(int i = 0; i < P.length(); i++){
            L[P.charAt(i)] = i;
        }

        return L;
    }
    
    public int countKeyword(String keyword) throws IOException{
		if (content == null){
		    content = fetchContent().toUpperCase();
		}
		
		//To do a case-insensitive search, we turn the whole content and keyword into upper-case:
		keyword = keyword.toUpperCase();
	
		int retVal = 0; 
		// 1. calculates appearances of keyword (Bonus: Implement Boyer-Moore Algorithm)
        int i = 0;
        while(i < content.length()){
            int j   = BoyerMoore(content, keyword, i);
            if(j == -1){
                break;
            }
            else{
                retVal ++;
                i = j + 1;
            }
        }
		return retVal;
    }
}
