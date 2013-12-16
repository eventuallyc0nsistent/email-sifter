package com.sifter.email.lib;
import java.util.*;
import java.io.*;
import java.net.*;

import com.sifter.email.model.*;

import gate.*;
import gate.creole.*;
import gate.util.*;
import gate.util.persistence.PersistenceManager;
import gate.corpora.RepositioningInfo;

/**
 * Class to get all data from the document using GATE modules. 
 * @author svalmiki
 *
 */
public class GateResources {

	private SerialAnalyserController serAnCtrlr= null;
	Corpus corpus = null;
	private static GateResources instance = null;

	private HashSet <String> reqAnnots = new HashSet<String>();
	
	
	
	/**
	 * Singleton constructor to initialize GATE. This takes a couple of seconds,
	 * and we do not want to do this for each instance
	 * @throws GateException
	 */
	protected GateResources() throws GateException{
		Out.prln(getClass().getResource("/").getPath());
		Gate.setGateHome(new File(getClass().getResource("/").getPath()));
		File gateHome = Gate.getGateHome();
		File pluginsHome = new File(gateHome,"plugins");
		Gate.setPluginsHome(pluginsHome);
		Gate.init();
		serAnCtrlr = new SerialAnalyserController();	
		
	}

	/**
	 * Generates a singleton instance
	 * @return
	 * @throws GateException
	 */
	public static GateResources getInstance() throws GateException{
		if(instance == null){
			instance = new GateResources();
		}

		return instance;
	}
	/**
	 * Initialize ANNIE and other processing resources here
	 */
	public void initialize()
	{
		try {
			serAnCtrlr = (SerialAnalyserController)PersistenceManager.loadObjectFromFile(new File(getClass().getResource("/plugins/ANNIE/"+ANNIEConstants.DEFAULT_FILE).getPath()));
			FeatureMap transducerParam=Factory.newFeatureMap();

			//get the jape transducer that was built
			transducerParam.put("grammarURL", getClass().getResource("/jape/main.jape"));
			ProcessingResource japeTransducer=(ProcessingResource) Factory.createResource("gate.creole.Transducer", transducerParam);
			/*        ProcessingResource pr1 = (ProcessingResource) Factory.createResource("gate.opennlp.OpenNlpTokenizer", params);
                        ProcessingResource pr2 = (ProcessingResource) Factory.createResource("gate.opennlp.OpenNlpSentenceSplit", params);
                        ProcessingResource pr3 = (ProcessingResource) Factory.createResource("com.ontotext.gate.gazetteer.HashGazetteer",gazparam);
                        ProcessingResource pr4 = (ProcessingResource) Factory.createResource("gate.opennlp.OpenNlpPOS", params);
                        ProcessingResource pr5 = (ProcessingResource) Factory.createResource("gate.creole.Transducer",transducerparam);
			 */
			serAnCtrlr.add(japeTransducer);
			
			
			Out.prln("Processing resources are loaded");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	private void setCorpus() {
		serAnCtrlr.setCorpus(corpus);             
	}
	public void execute() throws GateException {
		
		serAnCtrlr.execute();
		Out.prln("Executed.");
	}
	
	
	public void buildCorpusWithDoc(URL u) throws ResourceInstantiationException{
		corpus = (Corpus) Factory.createResource("gate.corpora.CorpusImpl");
		corpus.clear();
		FeatureMap params = Factory.newFeatureMap();
		params.put("sourceUrl", u);
		params.put("preserveOriginalContent", new Boolean(true));
		params.put("collectRepositioningInfo", new Boolean(true));
		Out.prln("Creating doc for " + u);
		Document temp = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
		corpus.add(temp);
		setCorpus();
	}
	
	
	public void buildCorpusWithDoc(File file) throws ResourceInstantiationException, MalformedURLException{
		corpus = (Corpus) Factory.createResource("gate.corpora.CorpusImpl");
		corpus.clear();
		FeatureMap params = Factory.newFeatureMap();
		URL u = file.toURI().toURL();
		params.put("sourceUrl", u);
		params.put("preserveOriginalContent", new Boolean(true));
		params.put("collectRepositioningInfo", new Boolean(true));
		Out.prln("Creating doc for " + u);
		Document temp = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
		corpus.add(temp);
		setCorpus();
	}
	
	public void setRequiredAnnots(HashSet<String> list){
		reqAnnots.clear();
		reqAnnots.addAll(list);
	}
	/**
	 * Default setter
	 * @return
	 */
	public HashSet<Annotation> getAnnotations(){
		if(corpus.iterator().hasNext()){
			Document doc = corpus.iterator().next();
			return new HashSet<Annotation>(doc.getAnnotations().get(reqAnnots));
		}
		else
			return null;
	}
	/**
	 * Gets specific annotations
	 * @param type
	 * @return
	 */
	public HashSet<Annotation> getAnnotations(String type){
		if(corpus.iterator().hasNext()){
			Document doc = corpus.iterator().next();
			return new HashSet<Annotation>(doc.getAnnotations().get(type));
		}
		else
			return null;
	}
	
	public String getContentFromCategory(Annotation annot, String cat) throws Exception{	
		if(annot.getFeatures().get(Constants.CATEGORY).equals(cat)){
			//return (String)annot.getFeatures().get(Constants.STRING);
			return corpus.get(0).getContent().getContent(annot.getStartNode().getOffset().longValue(),annot.getEndNode().getOffset().longValue()).toString();
		}
		
//		for(Map.Entry<Object,Object> f : annot.getFeatures().entrySet()){
//			if(f.getKey().equals("category")){
//				if(f.getValue().equals(cat)){
//					corpus.get(0).getContent().getContent(annot.getStartNode().getOffset().longValue(),annot.getEndNode().getOffset().longValue());
//				}
//			}
//		}
		return null;
	}
	
	
	public String getContentFromAnnot(Annotation annot) throws Exception{	
		//return (String)annot.getFeatures().get(Constants.STRING);
		return corpus.get(0).getContent().getContent(annot.getStartNode().getOffset().longValue(),annot.getEndNode().getOffset().longValue()).toString();
	}
	
	public String getContentFromKind(Annotation annot, String kind) throws InvalidOffsetException{
		if(annot.getFeatures().get(Constants.KIND).equals(kind)){
			//return (String)annot.getFeatures().get(Constants.STRING);
			return corpus.get(0).getContent().getContent(annot.getStartNode().getOffset().longValue(),annot.getEndNode().getOffset().longValue()).toString();
		}
		return null;
	}
	
	public void freeResources(){
		corpus.cleanup();
		
	}
	
	/**
	 * Unit test everything here before you push stuff to DAO
	 * @param a
	 * @throws Exception 
	 */
	
	public static void main(String a[]) throws Exception
	{
		
		
		com.sifter.email.model.EmailThread thread = new EmailThread();
		GateResources gr = GateResources.getInstance();
		gr.initialize();
		gr.buildCorpusWithDoc(GateResources.class.getResource("/docs/Gigzolo rehearsal.pdf"));
		gr.execute();
		//Get the subject
		HashSet<Annotation> subjAnnotSet = gr.getAnnotations("SubjectMail");
		if(subjAnnotSet.iterator().hasNext()){
			Annotation subjAnnot = subjAnnotSet.iterator().next();
			thread.setSubject(gr.getContentFromAnnot(subjAnnot));
			Out.prln(gr.getContentFromAnnot(subjAnnot));
		}
		
		Out.prln("\n\n Thread Part \n\n");
		HashSet<Annotation> threadPartAnnotSet = gr.getAnnotations("ThreadPart");
		thread.clearThreadParts();
		ThreadPart tp = new ThreadPart();
		
		StanfordResources stanfordParser = new StanfordResources();
		
//		for(Annotation tpAnnot : threadPartAnnotSet){
//			if(gr.getContent(tpAnnot,CategoryEnum.ThreadBody.getCategory()) != null){
//				tp.setBody(gr.getContent(tpAnnot,CategoryEnum.ThreadBody.getCategory()));
//				Out.prln("<Body>: "+tp.getBody()+"\n");
//				thread.addThreadPart(tp);
//				
//				stanfordParser.setThreadPart(tp.getBody());
//				stanfordParser.parseThreadPart();
//				
//				tp = new ThreadPart();
//				
//				
//			}
//			if(gr.getContent(tpAnnot,CategoryEnum.FromEmail.getCategory()) != null){
//				tp.setSenderEmail(gr.getContent(tpAnnot,CategoryEnum.FromEmail.getCategory()));
//				Out.prln("<SenderEmail>: "+tp.getSenderEmail()+"\n");
//			}
//			if(gr.getContent(tpAnnot,CategoryEnum.SentDate.getCategory()) != null){
//				tp.setSentTime(gr.getContent(tpAnnot,CategoryEnum.SentDate.getCategory()));
//				Out.prln("<SentTime>: "+tp.getSentTime()+"\n");
//			}
//			if(gr.getContent(tpAnnot,CategoryEnum.SenderName.getCategory()) != null){
//				tp.setSenderName(gr.getContent(tpAnnot,CategoryEnum.SenderName.getCategory()));
//				Out.prln("<SenderName> "+tp.getSenderName()+"\n");
//			}
//			
//		}
		
		SortedAnnotationList sortedAnnots = new SortedAnnotationList();
		
		for(Annotation an: threadPartAnnotSet){
			sortedAnnots.addSortedExclusive(an);
		}
		//ThreadPart tp = new ThreadPart();
		for (int i = 0; i < sortedAnnots.size(); ++i) {
			Annotation tpAnnot = (Annotation) sortedAnnots.get(i);
			if(gr.getContentFromCategory(tpAnnot,CategoryEnum.ThreadBody.getCategory()) != null){
				tp.setBody(gr.getContentFromCategory(tpAnnot,CategoryEnum.ThreadBody.getCategory()));
				
				// remove [Quoted  text  hidden]
				String removedQuotedText = tp.getBody().replace("[Quoted  text  hidden]", "");
				stanfordParser.setThreadPart(removedQuotedText);
				stanfordParser.parseThreadPart();

				thread.addThreadPart(tp);

			}
			if(gr.getContentFromCategory(tpAnnot,CategoryEnum.FromEmail.getCategory()) != null){
				tp.setSenderEmail(gr.getContentFromCategory(tpAnnot,CategoryEnum.FromEmail.getCategory()));
			}
			if(gr.getContentFromCategory(tpAnnot,CategoryEnum.SentDate.getCategory()) != null){
				tp.setSentTime(gr.getContentFromCategory(tpAnnot,CategoryEnum.SentDate.getCategory()));
			}
			if(gr.getContentFromCategory(tpAnnot,CategoryEnum.SenderName.getCategory()) != null){
				tp.setSenderName(gr.getContentFromCategory(tpAnnot,CategoryEnum.SenderName.getCategory()));
			}
		}
		
		
		
		
		
		
		
//		try {
//			//File gateHome = Gate.getGateHome();
//			GateResources glp = GateResources.getInstance();
//
//			glp.initialize();
//			Corpus corpus = (Corpus) Factory
//					.createResource("gate.corpora.CorpusImpl");
//			//File[] files = new File(GateResources.class.getResource("/docs/").getPath()).listFiles();
//
//			URL u = null;
//			u = GateResources.class.getResource("/docs/Gigzolo rehearsal.pdf");
//			FeatureMap params = Factory.newFeatureMap();
//			params.put("sourceUrl", u);
//			params.put("preserveOriginalContent", new Boolean(true));
//			params.put("collectRepositioningInfo", new Boolean(true));
//			Out.prln("Creating doc for " + u);
//			Document temp = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
//			corpus.add(temp);
//
//
//
//			glp.setCorpus();
//			glp.execute();
//			Iterator iter = corpus.iterator();
//			int count = 0;
////			String startTagPart_1 = "<span GateID=\"";
////			String startTagPart_2 = "\" title=\"";
////			String startTagPart_3 = "\" style=\"color:Red;\">";
////			String endTag = "</span>";
//
//			while (iter.hasNext()) {
//				Document doc = (Document) iter.next();
//				DocumentContent dc=doc.getContent();
//
//				//String txt=
//				AnnotationSet defaultAnnotSet = doc.getAnnotations();
//				Set<String> annotTypesRequired = new HashSet<String>();
//				//annotTypesRequired.add("Email");
////				annotTypesRequired.add("SubjectMail");
//				annotTypesRequired.add("ThreadPart");
//				//annotTypesRequired.add("Thread");
//				Set<Annotation> peopleAndPlaces = new HashSet<Annotation>(defaultAnnotSet.get(annotTypesRequired));
//				
//				FeatureMap features = doc.getFeatures();
////				String originalContent = (String) features
////						.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
////				RepositioningInfo info = (RepositioningInfo) features
////						.get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME);
////				++count;
//				//File file = new File("ANNIE_" + count + ".HTML");
//				//if (originalContent != null && info != null) {
//				Out.prln("OrigContent and reposInfo existing. Generate file...");
//				Iterator it = peopleAndPlaces.iterator();
//				Annotation currAnnot;
//				SortedAnnotationList sortedAnnotations = new SortedAnnotationList();
//				while (it.hasNext()) {
//					currAnnot = (Annotation) it.next();
//					Out.prln("<"+currAnnot.getType() +">:  "+dc.getContent(currAnnot.getStartNode().getOffset().longValue(),currAnnot.getEndNode().getOffset().longValue())+"\n\n");
//					FeatureMap fm = currAnnot.getFeatures();
//					for (Map.Entry<Object, Object> e : fm.entrySet()) {
//						Out.prln("Type: "+e.getKey()+"  Value: " + e.getValue());                                        
//					}
//					sortedAnnotations.addSortedExclusive(currAnnot);
//				} // while
//				String xmlDocument = doc.toXml(peopleAndPlaces, false);
//				//System.out.println(xmlDocument);
//				String fileName = new String("GATE" + count + ".HTML");
//				FileWriter writer = new FileWriter(fileName);
//				writer.write(xmlDocument);
//				writer.close();
//
//				// do something useful with the XML here!
//				// Out.prln("'"+xmlDocument+"'");
//			} // for each doc
//		} catch (GateException e) {
//			e.printStackTrace();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/**
	 * References: Gate Resources
	 * @author svalmiki
	 *
	 */
	public static class SortedAnnotationList extends Vector {
		public SortedAnnotationList() {
			super();
		} // SortedAnnotationList

		public boolean addSortedExclusive(Annotation annot) {
			Annotation currAnot = null;
			// overlapping check
			for (int i = 0; i < size(); ++i) {
				currAnot = (Annotation) get(i);
				if (annot.overlaps(currAnot)) {
					return false;
				} // if
			} // for
			long annotStart = annot.getStartNode().getOffset().longValue();
			long currStart;
			// insert
			for (int i = 0; i < size(); ++i) {
				currAnot = (Annotation) get(i);
				currStart = currAnot.getStartNode().getOffset().longValue();
				if (annotStart < currStart) {
					insertElementAt(annot, i);
					/*
					 * Out.prln("Insert start: "+annotStart+" at position: "+i+
					 * " size="+size()); Out.prln("Current start: "+currStart);
					 */
					return true;
				} // if
			} // for
			int size = size();
			insertElementAt(annot, size);
			// Out.prln("Insert start: "+annotStart+" at size position: "+size);
			return true;
		} // addSorted
	} // SortedAnnotationList
} // class StandAloneAnnie