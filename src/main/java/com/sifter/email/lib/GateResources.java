package com.sifter.email.lib;
import java.util.*;
import java.io.*;
import java.net.*;

import gate.*;
import gate.creole.*;
import gate.util.*;
import gate.corpora.RepositioningInfo;
public class GateResources {

	private SerialAnalyserController sac= null;
	private SerialAnalyserController sac2= null;
	private static GateResources instance = null;

	//Creating a singleton
	protected GateResources() throws GateException{
		Gate.setGateHome(new File(getClass().getResource("/").getPath()));
		File gateHome = Gate.getGateHome();
		File pluginsHome = new File(gateHome,"plugins");
		Gate.setPluginsHome(pluginsHome);
		Gate.init();
		sac = new SerialAnalyserController();
		sac2 = new SerialAnalyserController();
	}


	public static GateResources getInstance() throws GateException{
		if(instance == null){
			instance = new GateResources();
		}

		return instance;
	}

	public void initialize()
	{
		try {

			//Gate.init();

			//Gate.getCreoleRegister().registerDirectories(new File(Gate.getGateHome().getAbsolutePath()+ "resources/plugins/ANNIE/").toURI().toURL());
			Gate.getCreoleRegister().registerDirectories(getClass().getResource("/plugins/ANNIE"));


			sac=(SerialAnalyserController)Factory.createResource("gate.creole.SerialAnalyserController",Factory.newFeatureMap(),Factory.newFeatureMap(),"ANNIE+"+Gate.genSym());
			sac2=(SerialAnalyserController)Factory.createResource("gate.creole.SerialAnalyserController",Factory.newFeatureMap(),Factory.newFeatureMap());
			FeatureMap params=Factory.newFeatureMap();
			FeatureMap transducerparam=Factory.newFeatureMap();
			
			//get the jape transducer that was built
			transducerparam.put("grammarURL", getClass().getResource("/jape/main.jape"));
			ProcessingResource pr1=(ProcessingResource) Factory.createResource("gate.creole.annotdelete.AnnotationDeletePR", params);
			ProcessingResource pr2=(ProcessingResource) Factory.createResource("gate.creole.tokeniser.DefaultTokeniser", params);
			
			
			//Assign any gazetteers here
			//FeatureMap gazparam=Factory.newFeatureMap();
			//gazparam.put("listsURL", getClass().getResource("/plugins/gaz/xyz.def"));
			//ProcessingResource pr3=(ProcessingResource) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer", gazparam);
			
			
			
			ProcessingResource pr4=(ProcessingResource) Factory.createResource("gate.creole.splitter.SentenceSplitter", params);
			ProcessingResource pr5=(ProcessingResource) Factory.createResource("gate.creole.Transducer", transducerparam);
			/*        ProcessingResource pr1 = (ProcessingResource) Factory.createResource("gate.opennlp.OpenNlpTokenizer", params);
                        ProcessingResource pr2 = (ProcessingResource) Factory.createResource("gate.opennlp.OpenNlpSentenceSplit", params);
                        ProcessingResource pr3 = (ProcessingResource) Factory.createResource("com.ontotext.gate.gazetteer.HashGazetteer",gazparam);
                        ProcessingResource pr4 = (ProcessingResource) Factory.createResource("gate.opennlp.OpenNlpPOS", params);
                        ProcessingResource pr5 = (ProcessingResource) Factory.createResource("gate.creole.Transducer",transducerparam);
			 */

			sac.add(pr1);
			sac.add(pr2);
			//sac2.add(pr3);
			sac2.add(pr4);
			sac2.add(pr5);
			Out.prln("Processing resources are loaded");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	public void setCorpus(Corpus corpus) {
		sac.setCorpus(corpus);
		sac2.setCorpus(corpus);                
	}
	public void execute() throws GateException {
		Out.prln("Running");
		sac.execute();
		sac2.execute();
	}
	public static void main(String a[]) throws MalformedURLException
	{
		try {
			File gateHome = Gate.getGateHome();
			GateResources glp = GateResources.getInstance();

			glp.initialize();
			Corpus corpus = (Corpus) Factory
					.createResource("gate.corpora.CorpusImpl");
			File[] files = new File(GateResources.class.getResource("/docs/").getPath()).listFiles();
			//			for (File s : files) {
			//				//if(s.getName().endsWith(".txt"))
			//				//{
			//				URL u = null;
			//				try {
			//					u = new URL("file://" + s.get);
			//					FeatureMap params = Factory.newFeatureMap();
			//					params.put("sourceUrl", u);
			//					params.put("preserveOriginalContent", new Boolean(true));
			//					params.put("collectRepositioningInfo", new Boolean(true));
			//					Out.prln("Creating doc for " + u);
			//					Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
			//					corpus.add(doc);
			//				} catch (MalformedURLException m) {
			//					System.out.print(u);
			//					m.printStackTrace();
			//				}
			//				//}
			//			}

			URL u = null;
			u = GateResources.class.getResource("/docs/Introduction.pdf");
			FeatureMap params = Factory.newFeatureMap();
			params.put("sourceUrl", u);
			params.put("preserveOriginalContent", new Boolean(true));
			params.put("collectRepositioningInfo", new Boolean(true));
			Out.prln("Creating doc for " + u);
			Document temp = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
			corpus.add(temp);



			glp.setCorpus(corpus);
			glp.execute();
			Iterator iter = corpus.iterator();
			int count = 0;
			String startTagPart_1 = "<span GateID=\"";
			String startTagPart_2 = "\" title=\"";
			String startTagPart_3 = "\" style=\"color:Red;\">";
			String endTag = "</span>";

			while (iter.hasNext()) {
				Document doc = (Document) iter.next();
				DocumentContent dc=doc.getContent();

				//String txt=
				AnnotationSet defaultAnnotSet = doc.getAnnotations();
				Set<String> annotTypesRequired = new HashSet<String>();
				annotTypesRequired.add("Sentence");
				annotTypesRequired.add("SubjectMail");
				annotTypesRequired.add("ThreadPart");
				annotTypesRequired.add("Thread");
//				annotTypesRequired.add("service");
//				annotTypesRequired.add("room");
//				annotTypesRequired.add("clean");
//				annotTypesRequired.add("location");
//				annotTypesRequired.add("value");


				// annotTypesRequired.add("Location");
				Set<Annotation> peopleAndPlaces = new HashSet<Annotation>(defaultAnnotSet.get(annotTypesRequired));
				FeatureMap features = doc.getFeatures();
				String originalContent = (String) features
						.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
				RepositioningInfo info = (RepositioningInfo) features
						.get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME);
				++count;
				File file = new File("ANNIE_" + count + ".HTML");
				//if (originalContent != null && info != null) {
				Out.prln("OrigContent and reposInfo existing. Generate file...");
				Iterator it = peopleAndPlaces.iterator();
				Annotation currAnnot;
				SortedAnnotationList sortedAnnotations = new SortedAnnotationList();
				while (it.hasNext()) {
					currAnnot = (Annotation) it.next();
					Out.prln(currAnnot.getType() +"  "+dc.getContent(currAnnot.getStartNode().getOffset().longValue(),currAnnot.getEndNode().getOffset().longValue()));
					FeatureMap fm = currAnnot.getFeatures();
					for (Map.Entry<Object, Object> e : fm.entrySet()) {
						Out.prln("Type "+e.getKey()+"  Value " + e.getValue());                                        
					}
					sortedAnnotations.addSortedExclusive(currAnnot);
				} // while
				String xmlDocument = doc.toXml(peopleAndPlaces, false);
				//System.out.println(xmlDocument);
				String fileName = new String("GATE" + count + ".HTML");
				FileWriter writer = new FileWriter(fileName);
				writer.write(xmlDocument);
				writer.close();

				// do something useful with the XML here!
				// Out.prln("'"+xmlDocument+"'");
			} // for each doc
		} catch (GateException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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