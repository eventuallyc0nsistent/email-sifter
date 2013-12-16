package com.sifter.email.lib;
import com.sifter.email.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.io.StringReader;

import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.models.lexparser.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import gate.util.Out;

/*
 * To use the Stanford Parser for every body that is received
 * @author : Kiran K.
 */
public class StanfordResources {


	private static StanfordResources instance = null;
	private StanfordCoreNLP pipeline;
	private StanfordCoreNLP nerPipeline;
	private LexicalizedParser lp = null;
	private String tp;
	
	// This option shows loading and sentence-segmenting and tokenizing
	// a file using DocumentPreprocessor.
	private TreebankLanguagePack tlp;
	private GrammaticalStructureFactory gsf;

	protected StanfordResources()
	{
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
		pipeline = new StanfordCoreNLP(props);
//		Properties nerProps = new Properties();
//		nerProps.put("annotators", "tokenize,ssplit,pos, ner");
//		nerPipeline = new StanfordCoreNLP(nerProps);
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		tlp = new PennTreebankLanguagePack();
		gsf  = tlp.grammaticalStructureFactory();
	}
	
	public static StanfordResources getInstance(){
		if(instance ==  null){
			instance = new StanfordResources();
		}
		return instance;
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


	public void buildPhrases(ArrayList<String> phrases, String text){
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		List<CoreLabel> rawWords = tokenizerFactory.getTokenizer(new StringReader(text)).tokenize();
		Tree currTree = lp.apply(rawWords);

		for(Tree sentence: getSentenceTrees(text)){
			ArrayList<Tree> treeList = new ArrayList<Tree>();
			getPhrases(treeList, sentence);

			for(Tree t : treeList){
				TreebankLanguagePack tlp = new PennTreebankLanguagePack();
				GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
				GrammaticalStructure gs = gsf.newGrammaticalStructure(t);
				StringBuilder sb = new StringBuilder();
				boolean hasStarted = false;
				List<TreeGraphNode> graphNodes = new ArrayList<TreeGraphNode>(gs.getNodes());
				Collections.sort(graphNodes, new TreeGraphComparator());
				for(TreeGraphNode tgn: graphNodes){
					//System.out.println(tgn.label().word());
					String word = tgn.label().word();

					if(word != null){
						if(word.matches("[\\dA-Za-z ]*[/:-]?[\\dA-Za-z ]*[/-:]?[\\dA-Za-z& ]+") && hasStarted){
							sb.append(" ");
						}
						hasStarted = true;
						sb.append(word);

					}
				}
				phrases.add(sb.toString());
//				Out.prln(sb);
//				Out.prln();
			}
		}
	}




	private void getPhrases(ArrayList<Tree> treeList, Tree currTree){
		if(currTree == null){
			//Out.prln(currTree.size());
			return;
		}
		for(Tree t:currTree.getChildrenAsList()){

//			if(t.label().toString().equals("VB") || t.label().toString().equals("PRP")) {
//				treeList.add(t);
//			}
//			else if(t.label().toString().equals("NNP") && Character.isUpperCase(t.toString().split(" ")[1].charAt(0))) {
//				treeList.add(t);
//			}
//			else 
			if(t.label().toString().equals("NP") &&  t.size()>5 && t.size()<32){
				treeList.add(t);
			}
			else if(t.label().toString().equals("VP") &&  t.size()>5 && t.size()<32){
				treeList.add(t);
			}
			else getPhrases(treeList,t);
		}
//		for(Tree t:currTree.getChildrenAsList()){
//			getPhrases(treeList,t);
//		}
	}

	//	private void getPhrases(ArrayList<Tree> treeList, Tree currTree, String pos){
	//		if(currTree == null){
	//			return;
	//		}
	//		else if(currTree.label().toString().equals(pos)){
	//			treeList.add(currTree);
	//		}
	//
	//		else{
	//			for(Tree t:currTree.getChildrenAsList()){
	//				getPhrases(treeList,t,pos);
	//			}
	//		}
	//	}


	private ArrayList<Tree> getSentenceTrees(String text){
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		ArrayList<Tree> trees = new ArrayList<Tree>();
		for(CoreMap sentence: sentences) {
			Tree tree = sentence.get(TreeAnnotation.class);
			trees.add(tree);
		}
		return trees;

	}

	
	
	public int getNamedEntityScore(String text){
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		List<CoreMap> parts = document.get(SentencesAnnotation.class);
		int score = 0;
		for(CoreMap part: parts) {
			for (CoreLabel token: part.get(TokensAnnotation.class)) {
				String ne = token.get(NamedEntityTagAnnotation.class); 
				//System.out.println(word +"="+ne );
				if(ne.equals(NEEnum.DATE.name())){
					score += NEEnum.DATE.score();
				}
				else if(ne.equals(NEEnum.DURATION.name())){
					score += NEEnum.DURATION.score();
				}
				else if(ne.equals(NEEnum.LOCATION.name())){
					score += NEEnum.LOCATION.score();
				}
				else if(ne.equals(NEEnum.MONEY.name())){
					score += NEEnum.MONEY.score();
				}
				else if(ne.equals(NEEnum.NUMBER.name())){
					score += NEEnum.NUMBER.score();
				}
				else if(ne.equals(NEEnum.ORGANIZATION.name())){
					score += NEEnum.ORGANIZATION.score();
				}
				else if(ne.equals(NEEnum.PERSON.name())){
					score += NEEnum.PERSON.score();
				}
				else if(ne.equals(NEEnum.TIME.name())){
					score += NEEnum.TIME.score();
				}
			}
		}
		return score;
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
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			StringBuilder sb = new StringBuilder();
			//		    for(TreeGraphNode t: gs.getNodes()){
			//		    	System.out.println(t.label().word());
			//		    }
			//List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
			//		    System.out.println();
			//		    System.out.println(tdl);
			//		    System.out.println();

			Out.prln("******************NP*****************************");
			ArrayList<String> list = new ArrayList<String>();
			buildPhrases(list,parseSentence);
			for(String s: list){
				System.out.println(s);
			}
			
			Out.prln("******************/NP*****************************");
			Out.prln();

//			Out.prln("******************VP*****************************");
//			getPhrases(parseSentence,"VP");
//			Out.prln("******************/VP*****************************");
//			TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
			//tp.printTree(parse);

			//tp.

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	class TreeGraphComparator implements Comparator<TreeGraphNode> {

		@Override
		public int compare(TreeGraphNode arg0, TreeGraphNode arg1) {

			return arg0.index() - arg1.index();
		}
	}

}


