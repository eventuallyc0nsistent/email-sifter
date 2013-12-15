package com.sifter.email.lib;
import com.sifter.email.model.*;

import java.util.ArrayList;
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
public class StanfordResources {
	
	private LexicalizedParser lp = null;
	private String tp;
	
	// This option shows loading and sentence-segmenting and tokenizing
    // a file using DocumentPreprocessor.
    private TreebankLanguagePack tlp;
    private GrammaticalStructureFactory gsf;
	
	public StanfordResources()
	{
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		tlp = new PennTreebankLanguagePack();
		gsf  = tlp.grammaticalStructureFactory();
	}
	
	public void setThreadPart(String threadPart)
	{
		tp = threadPart;
		Out.prln(threadPart);
	}
	
	public String getThreadPart()
	{
		return tp;
	}
	
	
	public ArrayList<String> getPhrases(String text, String pos){
		ArrayList<String> phrases = new ArrayList<String>();
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	    List<CoreLabel> rawWords = tokenizerFactory.getTokenizer(new StringReader(text)).tokenize();
	    Tree currTree = lp.apply(rawWords);
		ArrayList<Tree> treeList = new ArrayList<Tree>();
		getPhrases(treeList, currTree,pos);
		
		for(Tree t : treeList){
			Out.prln(t.toString());
			Out.prln();
		}
		
		
		return phrases;
	}
	

	
	
	private void getPhrases(ArrayList<Tree> treeList, Tree currTree, String pos){
		if(currTree == null){
			return;
		}
		else if(currTree.label().toString().equals(pos)){
			treeList.add(currTree);
		}
		
		else{
			for(Tree t:currTree.getChildrenAsList()){
				getPhrases(treeList,t,pos);
			}
		}
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
		    //parse.
		    TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		    //GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		    //GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		    
		    //List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		    System.out.println();
		    //System.out.println(tdl);
		    System.out.println();
		    
		    Out.prln("******************NP*****************************");
		    getPhrases(parseSentence,"NP");
		    Out.prln("******************/NP*****************************");
		    Out.prln();
		    
		    Out.prln("******************VP*****************************");
		    getPhrases(parseSentence,"VP");
		    Out.prln("******************/VP*****************************");
		    //TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
		    //tp.printTree(parse);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}