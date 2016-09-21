package aspect_parser;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
//added package for iterating through sentences

import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.ling.CoreAnnotation;
public class aspect_extraction {
	ArrayList<String> aspectFrequency = new ArrayList<String>();
	
	String dependency;
	String pos_tags="";
	String ner="";
	Hashtable h=null;
	HashMap h1=null;
	HashMap h2=null;
	HashMap h3=null;
	
	 HashMap newmap = new HashMap();
	
	ArrayList<String>entities=new ArrayList<String>();
	public aspect_extraction()
	{
		doLoad();

	}
	
	private void doLoad() {    
        try {  
  
           // System.out.println("Creating File/Object input stream...");  
        	InputStream in1 = this.getClass().getClassLoader()
                    .getResourceAsStream("files/files/emotion_lexicon.ser");
            //FileInputStream fileIn = new FileInputStream(in);  
            ObjectInputStream in = new ObjectInputStream(in1);  
  
            //System.out.println("Loading Hashtable Object...");  
            h = (Hashtable<String,String>)in.readObject();  
  
            //System.out.println("Closing all input streams...\n");  
            in.close();  
            in1.close();  
            //FileInputStream fileIn = new FileInputStream("files/sentic/emotion_lexicon.ser");  
              
      //      System.out.println(h.size());
            //System.out.println("Printing out loaded elements...");  
          
              
        } catch (ClassNotFoundException e) {  
            e.printStackTrace();  
        } catch(FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        
        //System.out.println();  
  
    }  
	public void pre_processing(String input,StanfordCoreNLP  pipeline, LexicalizedParser lp)
	{
		pos_tags="";
		dependency="";
		try{
		input=input.replaceAll("--", "-");
		input=input.replaceAll("-", "-");
		Annotation document = pipeline.process(input);
		 //String line="";
		  for(CoreMap sentence: document.get(SentencesAnnotation.class)) {    
		    for(CoreLabel token: sentence.get(TokensAnnotation.class)) {  
		    	String word = token.get(TextAnnotation.class); 
		    	String pos = token.get(PartOfSpeechAnnotation.class);
		    	String NER=token.get(NamedEntityTagAnnotation.class);
		    	pos_tags=pos_tags+" "+word.toLowerCase()+"/"+pos;
		    	ner=ner+" "+word.toLowerCase()+"/"+NER;
		    }
		  }
		  pos_tags=pos_tags.trim();
		  TokenizerFactory<CoreLabel> tokenizerFactory =
			      PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
			    List<CoreLabel> rawWords2 =
			      tokenizerFactory.getTokenizer(new StringReader(input.trim())).tokenize();
			    Tree parse = lp.apply(rawWords2);
			    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		dependency = tdl.toString();
	//	System.out.println(dependency);
		//dependency=stan.lemmatize_parse(dependency,h,pos_tags);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		 LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

			Properties props = new Properties();
		 //   props.put("annotators", "tokenize, ssplit, pos, lemma"); // for only aspect parser
		     props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		   // props.put("regexner.mapping", "org/foo/resources/jg-regexner.txt");
		     StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		aspect_extraction asp=new aspect_extraction();
		File fs=new File("/Users/pritee/bodies1/");
//		String input = args[0];
//		File fs=new File(input);
		File fk[]=fs.listFiles();
		    //System.out.println(content);
		//The pizza was delicious but the waiter was rude
		//The touchscreen is good but the battery lasts very little
		
//	 String paragraph = "The pizza was delicious but the waiter was rude.";
		//Reader reader = new StringReader(paragraph);
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("file_index.txt")));
		for (int i=1;i<fk.length;i++)
//		for (int i=0;i<5000;i++)
	 {
//		System.out.println(i);
//		bw.write(fk[i]+"\n");
	bw.flush();
		DocumentPreprocessor dp = new DocumentPreprocessor(new BufferedReader(new FileReader(fk[i])));
		List<String> sentenceList = new ArrayList<String>();
		
		

		for (List<HasWord> sentence : dp) {	
		   String sentenceString = Sentence.listToString(sentence);
		
		   asp.get_aspects(sentenceString, pipeline, lp);
		   
		   
		}
	
	//	System.out.print("Enter something : ");
//		String input = args[0];
	//	 input = System.console().readLine();
  //      asp.get_aspects(input, pipeline, lp);
		
//		System.out.println(asp.aspectFrequency);
		
		Set<String> unique = new HashSet<String>(asp.aspectFrequency);
	//	for (String key : unique) {
//		    System.out.println(key + ": " + Collections.frequency(asp.aspectFrequency, key));
		//}
	
		String fileName = "./ank6.txt";

		try {	           
			FileWriter fileWriter = new FileWriter(fileName);
			// Always wrap FileWriter in BufferedWriter.
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			// Note that write() does not automatically append a newline character.
			bufferedWriter.write("Key" +"\t"+"frequency");
			bufferedWriter.newLine();		           
		            
			for(String key : unique) {
				bufferedWriter.write(key +"\t"+Collections.frequency(asp.aspectFrequency, key));
				bufferedWriter.newLine();		           
			}
			// Always close files.
			bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println("Error writing to file '"  + fileName + "'");	           
		} 
		
	}
	}
	public ArrayList<String> get_aspects(String line, StanfordCoreNLP pipeline, LexicalizedParser lp) throws IOException
	{
		ArrayList<String> aspects=new ArrayList<String>();
		//System.out.println("Line here is ---- "+line);
		try{
		if(!line.endsWith(".")&&!line.endsWith("?")&&!line.endsWith("!"))
		line=line+".";
		line=line.replaceAll("-", "-");
		pre_processing(line,pipeline,lp);
		String parse = dependency.toLowerCase();
		line=line.toLowerCase();
		String pos = pos_tags;	
		String newString="";
		if(parse==null||pos==null || parse.equals("[]"))return null;
		aspects asp = new aspects(parse, pos,h);
		ArrayList<String> final_aspects = new ArrayList<String>();
		final_aspects = asp.get_aspects();
		ArrayList<String> ar2=new ArrayList<String>();
		ar2=final_aspects;
		aspects=final_aspects;
//		System.out.println(pos);
		int size= aspects.size();
		for(int i=size-1;i>=0;i--)
		{
			newString=aspects.get(i);
		  	 if (  pos.contains(newString+"/VBZ")   ||   pos.contains(newString+"/VB") || pos.contains(newString+"/DT") || pos.contains(newString+"/RP"))  
			        aspects.remove(newString);
		
		}
		
		
//		System.out.println("Aspects--"+aspects);
		aspectFrequency.addAll(aspects);	
		BufferedWriter asp1=new BufferedWriter(new FileWriter(new File("asp.txt"),true));
		asp1.write(aspects+"\n");
		asp1.flush();

		if(aspects.size()==0)
		{
//			System.out.println("Aspects not present");
		}
		final_aspects=null;
		ar2=null;
		newmap.put(line, aspects);
//	    String key = newmap.getKey().toString();
//	    Integer value = newmap.getValue();
//	    System.out.println("key, " + key + " value " + value);
	    System.out.println(newmap);
	    newmap.clear();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return aspects;
	}
	
	
}
