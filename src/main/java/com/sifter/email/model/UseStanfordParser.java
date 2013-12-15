package com.sifter.email.model;

import java.util.Collection;
import java.util.List;
import java.io.StringReader;

import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.models.lexparser.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import gate.util.Out;

/*
 * To use the Stanford Parser for every body that is received
 * @author : Kiran K.
 */
public class UseStanfordParser {
	
	private LexicalizedParser lp ;
	private String tp;
	
	// This option shows loading and sentence-segmenting and tokenizing
    // a file using DocumentPreprocessor.
    private TreebankLanguagePack tlp;
    private GrammaticalStructureFactory gsf;
	
	public UseStanfordParser()
	{
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		tlp = new PennTreebankLanguagePack();
		gsf  = tlp.grammaticalStructureFactory();
	}
	
	public void setThreadPart(String threadPart)
	{
		tp = threadPart;
	}
	
	public String getThreadPart()
	{
		return tp;
	}
	
	public void parseThreadPart()
	{
		try
		{
			String parseSentence = getThreadPart();
			
			// This option shows loading and using an explicit tokenizer
		    TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		    List<CoreLabel> rawWords2 = tokenizerFactory.getTokenizer(new StringReader(parseSentence)).tokenize();
		    Tree parse = lp.apply(rawWords2);
	
		    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		    System.out.println(tdl);
		    
		    for(TypedDependency td : tdl)
		    {
		    	System.out.println(td);
		    	// print relation
		    	System.out.println(td.reln());
		    	// print governer word
		    	System.out.println(td.gov());
		    	//print dependent word
		    	System.out.println(td.dep());
		    }
		    
	
		    // explicit Tree print by Stanford Parser
		    TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
		    tp.printTree(parse);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
