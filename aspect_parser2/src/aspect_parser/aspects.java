package aspect_parser;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class aspects {

	HashMap<Integer, String> pos_index = null;
	HashMap<Integer,String> word_index = null;
	ArrayList<String> aspects=null;
	ArrayList<String> mod_aspects=null;
	ArrayList<String> ar = null;
	//ArrayList<String> jj_aspects = null;
	ArrayList<String> known = null;
	ArrayList<String> visited = null;
	ArrayList<String> nsubj=null;
	ArrayList<String> modifier=null;
	ArrayList<String> dobj=null;
	String _strTkenizedInput="";
	Hashtable h=null;
	HashMap h1=null;
	HashMap<String,String> graph = null;
	private HashMap h2;
	
	public void dependency_graph_maker(String parse) throws Exception
	{
		String input = parse.replaceAll(Pattern.quote("["), "");
		input = input.replaceAll(Pattern.quote("]"), "");
		String relations[]= input.split(Pattern.quote("),"));
		for(int i=0;i<relations.length;i++)
		{
			//System.out.println(relations[i]);
			relations[i]=relations[i].trim();
			String relation = relations[i].substring(0,relations[i].indexOf("("));
			String first_ = relations[i].substring(relations[i].indexOf("(")+1,relations[i].indexOf(","));
			String second_ = relations[i].substring(relations[i].indexOf(",")+1,relations[i].length()).replaceAll(Pattern.quote(")"), "");
			if(relation.equals("nsubj")||relation.equals("cop"))
			{
				String word1=first_.substring(0, first_.lastIndexOf("-")).trim();
				String word2=second_.substring(0, second_.lastIndexOf("-")).trim();
//				if(!nsubj.contains(word1))nsubj.add(word1);
//				if(!nsubj.contains(word2))nsubj.add(word2);
			}
			else
				if(relation.equals("amod")||relation.equals("advmod"))
				{
					String word1=first_.substring(0, first_.lastIndexOf("-")).trim();
					String word2=second_.substring(0, second_.lastIndexOf("-")).trim();
					if(!modifier.contains(word1))modifier.add(word1);
					if(!modifier.contains(word2))modifier.add(word2);
				}
				else
					if(relation.equals("dobj"))
					{
						String word1=first_.substring(0, first_.lastIndexOf("-")).trim();
						String word2=second_.substring(0, second_.lastIndexOf("-")).trim();
						if(!dobj.contains(word1))dobj.add(word1);
						if(!dobj.contains(word2))dobj.add(word2);
					}
			if(graph.containsKey(first_.trim()))
			{
				String get = graph.get(first_.trim());
				String put = get+"\t"+relation.trim()+" "+second_.trim();
				graph.put(first_.trim(), put.trim());
			}
			else
			{
				String put = relation.trim()+" "+second_.trim();
				graph.put(first_.trim(), put.trim());
			}
		}
	}
	public void aspects_new(String parse, String _strTkenizedInput,Hashtable<String,String> h) throws Exception
	{
	index_per_word(parse, _strTkenizedInput);
	String splited[]=_strTkenizedInput.split(" ");
	int check_nsubj=0;
	for (int count = 0 ; (count < splited.length)   ; count++ )
    {
    	if(splited[count].split("/").length<2)continue;
    	
    	 String word = splited[count].split("/")[0];
         String tag= splited[count].split("/")[1];
         if(graph.containsKey(word+"-"+(count+1)))   
         {
        	 String relations[]=graph.get(word+"-"+(count+1)).split("\t");
        	 int check=1; String nval=""; int mod_n=1;
        	 for(int i=0;i<relations.length;i++)
        	 {
        		 String relation = relations[i].split(" ")[0];
        		 String word_ = relations[i].split(" ")[1];
        		 if(relation.equals("nsubj"))
        		 {
        			 check_nsubj=1;
        			 nval=word_.substring(0,word_.indexOf("-"));
        			 check=0;
        			 aspects=graph_traversal(word+"-"+(count+1),parse,nval,0);
        		 }
        		 if(relation.equals("xsubj"))
        		 {
        			 check_nsubj=1;
        			 nval=word_.substring(0,word_.indexOf("-"));
        			 check=0;
        			 aspects=graph_traversal(word+"-"+(count+1),parse,nval,0);
        		 }
        		 else
        			 if(relation.equals("nsubjpass"))
            		 {
        				 check_nsubj=1;
            			 nval=word_.substring(0,word_.indexOf("-"));
            			 check=0;
            			 visited=new ArrayList<String>();
            					 
            			 aspects=graph_traversal(word+"-"+(count+1),parse,nval,0);
            		 }
        		 if((relation.equals("cop")||relation.equals("aux")) && check==0 && !pos_index.get(count+1).contains("NN"))
        		 {

        			if(h.containsKey(word)) 
        			 {       		            
        				  if(!aspects.contains(nval) && !nval.equals("i") && !nval.equals("it") && _strTkenizedInput.contains(nval+"/NN"))
        				 aspects.add(nval);
        			 }
        		 }
        		 else
        			 if((relation.equals("cop")||relation.equals("aux")) && check==0)
            		 {
        		
            			 h.put("nicer", "");
            		
            			 if(h.containsKey(word)) 
            			 {
            	
            				
     							if(hasModifier(word,parse))
     							{
     								String modd=extract_modifier(word,parse);
     							}
     							
     							if(parse.contains("vmod("+word+"-"))
           		             {
           		            	String strcomp1=parse.substring(parse.indexOf("vmod("+word+"-"),parse.indexOf(")",parse.indexOf("vmod("+word+"-")));
           		               	String target1= strcomp1.substring(strcomp1.indexOf(",")+1,strcomp1.indexOf("-",strcomp1.indexOf(","))).trim();
           		               	if(!aspects.contains(target1))aspects.add(target1);
           		             }
            			 }
            		 }
        	 }
         }
         
    }
	if(check_nsubj==0)
	for(int count = 0 ; (count < splited.length)   ; count++ )
	{
		if(splited[count].split("/").length<2)continue;
    	
   	 String word = splited[count].split("/")[0];
        String tag= splited[count].split("/")[1];
        if(graph.containsKey(word+"-"+(count+1)))
        {
       	 String relations[]=graph.get(word+"-"+(count+1)).split("\t");
       	 for(int i=0;i<relations.length;i++)
       	 {
       		 String relation = relations[i].split(" ")[0];
       		 String word_ = relations[i].split(" ")[1];
       		 String nval = word_.substring(0,word_.indexOf("-"));
       		 if(relation.equals("prep_of") ||relation.equals("prep_in") || relation.equals("prep_from") || relation.equals("prep_for"))
       		 {
       			 if(!aspects.contains(word) && pos_index.get(count+1).contains("NN"))aspects.add(word);
       			 if(!aspects.contains(nval) && pos_index.get(Integer.parseInt(word_.substring(word_.indexOf("-")+1,word_.length()))).contains("NN"))aspects.add(nval);

       		 }
       		if(parse.contains("dobj("+word+"-"))
	             {
					
	            	String strcomp1=parse.substring(parse.indexOf("dobj("+word+"-"),parse.indexOf(")",parse.indexOf("dobj("+word+"-")));
	               	String target1= strcomp1.substring(strcomp1.indexOf(",")+1,strcomp1.indexOf("-",strcomp1.indexOf(","))).trim();
	               	if(!aspects.contains(target1))aspects.add(target1);
	             }
       		if(parse.contains("dep("+word+"-"))
             {
			
            	String strcomp1=parse.substring(parse.indexOf("dep("+word+"-"),parse.indexOf(")",parse.indexOf("dep("+word+"-")));
               	String target1= strcomp1.substring(strcomp1.indexOf(",")+1,strcomp1.indexOf("-",strcomp1.indexOf(","))).trim();
               	if(!aspects.contains(target1))aspects.add(target1);
            	if(!aspects.contains(word))aspects.add(word);
             }
       		

       	 }
        } 
	}
	

	ArrayList<String> blacklist = new ArrayList<String>();
	
	for(int i=0;i<aspects.size();i++)
	{
		String word = aspects.get(i);
		if(blacklist.contains(word))continue;
		if(parse.contains("nn("+word+"-"))
        {
       	 
       	    String strcomp1=parse.substring(parse.indexOf("nn("+word+"-"),parse.indexOf(")",parse.indexOf("nn("+word+"-")));
           	String target1= strcomp1.substring(strcomp1.indexOf(",")+1,strcomp1.indexOf("-",strcomp1.indexOf(","))).trim();
           	if(!mod_aspects.contains(target1+" "+word))
           	{
           		mod_aspects.add(target1+" "+word);
           		blacklist.add(word);
           		blacklist.add(target1);
           	}
        }
           	else
           	{

           		if(!mod_aspects.contains(word))mod_aspects.add(word);
           	}
           
		 if(_strTkenizedInput.contains(word+"/V") && h.containsKey(word))
         {
        	 mod_aspects.remove(word);
         }
        
	
	}
	if(mod_aspects.size()==0 )
	{
		String splited1[]=_strTkenizedInput.split(" ");

		for (int count = 0 ; (count < splited1.length)   ; count++ )
        {
        	if(splited1[count].split("/").length<2)continue;
        	
        	 String word = splited1[count].split("/")[0];
             String tag= splited1[count].split("/")[1];
             if(hasModifier(word,parse))
             {
            	 mod_aspects.add(word);
             }
             if(parse.contains("nn("+word+"-"))mod_aspects.add(word);
             
                   }
		

	}
	for(int i=0;i<mod_aspects.size();i++)
	{
		String word = mod_aspects.get(i).toString();
		if(parse.contains("nn("+word+"-"))
        {
       	 //System.out.println("**********"+word);
       	    String strcomp1=parse.substring(parse.indexOf("nn("+word+"-"),parse.indexOf(")",parse.indexOf("nn("+word+"-")));
           	String target1= strcomp1.substring(strcomp1.indexOf(",")+1,strcomp1.indexOf("-",strcomp1.indexOf(","))).trim();
           	if(!mod_aspects.contains(target1+" "+word))
           	{
           		mod_aspects.remove(i);
           		mod_aspects.add(target1+" "+word);
           	}
        }
		
		if(parse.contains("conj_and("+word+"-"))
		{
			String strcomp2=parse.substring(parse.indexOf("conj_and("+word+"-"),parse.indexOf(")",parse.indexOf("conj_and("+word+"-")));
           	String target2= strcomp2.substring(strcomp2.indexOf(",")+1,strcomp2.indexOf("-",strcomp2.indexOf(","))).trim();
        	if(!mod_aspects.contains(target2) && _strTkenizedInput.contains(target2+"/NN"))
               	mod_aspects.add(target2);
		}
		if(parse.contains("appos("+word+"-"))
		{
			String strcomp2=parse.substring(parse.indexOf("appos("+word+"-"),parse.indexOf(")",parse.indexOf("appos("+word+"-")));
           	String target2= strcomp2.substring(strcomp2.indexOf(",")+1,strcomp2.indexOf("-",strcomp2.indexOf(","))).trim();
        	if(!mod_aspects.contains(target2) && _strTkenizedInput.contains(target2+"/NN"))
               	mod_aspects.add(target2);
		}
		if(parse.contains("prt("+word+"-"))
		{
			String strcomp2=parse.substring(parse.indexOf("prt("+word+"-"),parse.indexOf(")",parse.indexOf("prt("+word+"-")));
           	String target2= strcomp2.substring(strcomp2.indexOf(",")+1,strcomp2.indexOf("-",strcomp2.indexOf(","))).trim();
        	if(!mod_aspects.contains(target2) )
               	mod_aspects.add(target2);
		}
		
	}

}
	
	private ArrayList<String> graph_traversal(String word,String parse,String rem_nsub,int count) throws Exception {
		// TODO Auto-generated method stub
		if(parse.contains("cop("+word.substring(0,word.indexOf("-"))+"-"))
			//return aspects;
		if(count==21)return aspects;
		try{
		if(!graph.containsKey(word) || visited.contains(word))
		{
			return aspects;
		}
		else
		{
			visited.add(word);
			String relations[]=graph.get(word).split("\t");
		for(int i=0;i<relations.length;i++)
   	   {
   		 String relation = relations[i].split(" ")[0];
   		 String word_ = relations[i].split(" ")[1];
   		 if(visited.contains(word_))continue;
   		  	//	 System.out.println(word_);
   		 if((pos_index.containsKey(Integer.parseInt(word_.substring(word_.lastIndexOf("-")+1,word_.length())))))
   		 {
   		 if((pos_index.get(Integer.parseInt(word_.substring(word_.lastIndexOf("-")+1,word_.length()))).contains("NN") || pos_index.get(Integer.parseInt(word_.substring(word_.lastIndexOf("-")+1,word_.length()))).equals("PRP")) && !parse.contains("cop("+word.substring(0,word.indexOf("-"))+"-") && !parse.contains("aux("+word.substring(0,word.indexOf("-"))+"-"))
   		{
   			 if(relation.equals("nsubj"))
   			 {
   				 rem_nsub=word_.substring(0,word_.indexOf("-"));
   				String nval = word.substring(0,word.indexOf("-"));
   			//	 System.out.println(nval);
   				 if(_strTkenizedInput.contains(nval+"/VB") && !aspects.contains(nval))
   					aspects.add(nval);
   				 if(hasModifier(word.substring(0,word.indexOf("-")),parse) && !pos_index.get(Integer.parseInt(word.substring(word.lastIndexOf("-")+1,word.length()))).contains("NN") && !rem_nsub.equals("you") && !rem_nsub.equals("i"))
   				 {
	   					if(!aspects.contains(nval) && (_strTkenizedInput.contains(nval+"/NN") ||_strTkenizedInput.contains(nval+"/JJ")))
	   					{
	   						aspects.add(nval);
	   					}
	   					if(!aspects.contains(rem_nsub)  && (_strTkenizedInput.contains(rem_nsub+"/NN") ||_strTkenizedInput.contains(rem_nsub+"/JJ")))
	   						aspects.add(rem_nsub);
   					
   				 }
   			 }
   			 else
   				 if(relation.equals("nsubjpass"))
   	   			 {
   					rem_nsub=word_.substring(0,word_.indexOf("-"));
   	   				 if(hasModifier(word.substring(0,word.indexOf("-")),parse) && !pos_index.get(Integer.parseInt(word.substring(word.lastIndexOf("-")+1,word.length()))).contains("NN"))
   	   				 {
   	   					 String nval = word.substring(0,word.indexOf("-"));
   	   					if(!aspects.contains(nval) && (_strTkenizedInput.contains(nval+"/NN") ||_strTkenizedInput.contains(nval+"/JJ")))
   	   					{
   	   						aspects.add(nval);
   	   					}
	   					if(!aspects.contains(rem_nsub) && (_strTkenizedInput.contains(rem_nsub+"/NN") ||_strTkenizedInput.contains(rem_nsub+"/JJ")))aspects.add(rem_nsub);

   	   				 }
   	   			 }
   			
   			 else
   			 if(!aspects.contains(word_.substring(0,word_.indexOf("-"))) && (pos_index.get(Integer.parseInt(word_.substring(word_.lastIndexOf("-")+1,word_.length()))).contains("NN") || pos_index.get(Integer.parseInt(word_.substring(word_.lastIndexOf("-")+1,word_.length()))).equals("PRP")) && relation.equals("dobj") && (!rem_nsub.equals("i") && !rem_nsub.equals("you") && !_strTkenizedInput.contains(rem_nsub+"/NN")))
   			 {

   				 aspects.add(word_.substring(0,word_.indexOf("-")));
   				 if(hasModifier(word_.substring(0, word_.indexOf("-")),parse))
   				 {
   					
   					 String in=word_.substring(0, word_.indexOf("-"));
   					 if((_strTkenizedInput.contains(in+"/NN") ||_strTkenizedInput.contains(in+"/JJ")))
   					 aspects.add(in);
   				 }
   			 }
   			 else
   	   			 if(!aspects.contains(word_.substring(0,word_.indexOf("-"))) && (pos_index.get(Integer.parseInt(word_.substring(word_.lastIndexOf("-")+1,word_.length()))).contains("NN") || pos_index.get(Integer.parseInt(word_.substring(word_.lastIndexOf("-")+1,word_.length()))).equals("PRP")))
   	   			 {
   	   				 String nval = word_.substring(0,word_.indexOf("-"));
   	   				 if(h.containsKey(nval) && (parse.contains("prep_in("+nval+"-") || parse.contains("prep_of("+nval+"-") || parse.contains("prep_about("+nval+"-") || parse.contains("prep_for("+nval+"-")))
   	   				 {
   	   					 String rels[]=graph.get(word_).split("\t");
   	   					 for(int t=0;t<rels.length;t++)
   	   					 {
   	   						 if(rels[t].split(" ")[0].contains("prep_"))
   	   						 {
   	   							 String target = rels[t].split(" ")[1];
   	   							 if(!aspects.contains(target.substring(0,target.indexOf("-"))))
   	   							 {
   	   								 String in=target.substring(0, target.indexOf("-"));
   	   							if((_strTkenizedInput.contains(in+"/NN") ||_strTkenizedInput.contains(in+"/JJ")))
   	   								 aspects.add(in);
   	   							 }
	   								 String in=word_.substring(0, word_.indexOf("-"));
	    	   							if((_strTkenizedInput.contains(in+"/NN") ||_strTkenizedInput.contains(in+"/JJ")))

   	   						aspects.add(in);
   	   						 }
   	   					 }
   	   				 }
   	   				 else
   	   				if(pos_index.get(Integer.parseInt(word_.substring(word_.lastIndexOf("-")+1,word_.length()))).contains("NN"))
   	   				{
   	   				String in=word_.substring(0, word_.indexOf("-"));
						if((_strTkenizedInput.contains(in+"/NN") ||_strTkenizedInput.contains(in+"/JJ")))
							aspects.add(in);
   	   				}
   	   			 }
   	   			
   			 //return aspects;
   			 
				graph_traversal(word_,parse,rem_nsub,count++);

   		}
   		 else
  			 {
  				//System.out.println("****************"+word);
   			 if(pos_index.containsKey(pos_index.get(Integer.parseInt(word.substring(word.lastIndexOf("-")+1,word.length())))))
  				 if(pos_index.get(Integer.parseInt(word.substring(word.lastIndexOf("-")+1,word.length()))).contains("JJ"))aspects.add(word.substring(0,word.indexOf("-")));
   			String in=word_.substring(0, word_.indexOf("-"));
			if((_strTkenizedInput.contains(in+"/NN") ))
				aspects.add(in);	
   			 graph_traversal(word_,parse,rem_nsub,count++);
  			 }
   	   }
			
   		
   	 }
		}
		}catch(Exception e)
		{
			 System.out.println(pos_index);

			e.printStackTrace();
		}
		return aspects;
	}

	
	
	private void index_per_word(String parse, String tokenizer_out) throws Exception {
		// TODO Auto-generated method stub
		
		String splited[]=tokenizer_out.split(" ");
		for(int i=0;i<splited.length;i++)
		{
			String word_pos[]=splited[i].split("/");
			String word=word_pos[0];
			String pos=word_pos[1];
			if(pos.contains("NNS"))pos=pos.replace("NNS", "NN");
			else
				if(pos.contains("NNP"))pos=pos.replace("NNP", "NN");
			word_index.put(i+1,word);
			pos_index.put(i+1,pos);
		}
		
	}
	private boolean hasModifier(String word, String parse) {
		// TODO Auto-generated method stub
		if(parse.contains("amod("+word+"-")||parse.contains("advmod("+word+"-")||parse.contains("acomp("+word+"-"))
			return true;
		return false;
	}
	
	private String extract_modifier(String word, String parse) {
		// TODO Auto-generated method stub
		String modd=""; /*NEED MODIFICATION*/
        if (parse.contains("amod("+word)){
    		int k =parse.indexOf("amod("+word)+("amod("+word).length();
    		modd =parse.substring(parse.indexOf(",",k)+1 , parse.indexOf("-",parse.indexOf(",", k))).trim();
    	}
        else if (parse.contains("advmod("+word)){
    		int k =parse.indexOf("advmod("+word)+("advmod("+word).length();
    		modd=parse.substring(parse.indexOf(",",k)+1 , parse.indexOf("-",parse.indexOf(",", k))).trim();
    	}
        else if (parse.contains("acomp("+word)){
    		int k =parse.indexOf("acomp("+word)+("acomp("+word).length();
    		modd=parse.substring(parse.indexOf(",",k)+1 , parse.indexOf("-",parse.indexOf(",", k))).trim();
    	}
        return modd;
	}
	
	
	
	public aspects(String parse, String _strTkenizedInput,Hashtable<String,String> h) throws IOException
	{
	this.h=h;
	pos_index=	new HashMap<Integer,String>();
	word_index = new HashMap<Integer,String>();
	ar=new ArrayList<String>();;
	known=new ArrayList<String>();
	visited=new ArrayList<String>();
	aspects=null;
	aspects = new ArrayList<String>();
	mod_aspects = new ArrayList<String>();
	nsubj=new ArrayList<String>();
	modifier=new ArrayList<String>();
	dobj=new ArrayList<String>();

	graph=new HashMap<String,String>();
	this._strTkenizedInput = _strTkenizedInput;
	try{
		dependency_graph_maker(parse);
	
	
	aspects_new(parse, _strTkenizedInput, h);

}catch(Exception e)
	{
	e.printStackTrace();
	}
}
	
	
	public ArrayList get_aspects() throws IOException
	{

		return mod_aspects;
	}
	
	


}
