email-sifter
============
Natural Language Processing application to produce useful information from an e-mail chain.

Tagging emails as they arrive in threads. Threads can be long or short conversations between two or three people. Using the [gate](http://gate.ac.uk/) API we will be implementing the text analysis of the emails.

We will be following this branching model : [link](http://nvie.com/posts/a-successful-git-branching-model/)

Contributers:

+   Kiran Koduru
+   Lakshmi Duvvuri
+   Manas Pawar
+   Shreyas Valmiki


Requirements to import the project into Eclipse:
Maven

Ways to import project:
File => Import => Existing Maven Project

Ways to run in Eclipse:

1. As a RESTful web service:

	a. Run the Main.java
	b. Open browser and go to http://localhost:8000/sifter
	c. You should be able to see "Got it!". If you are, everything went right
	d. All searches you are about to make are from the test set within resources/testset/test directory
	e. Enter http://localhost:8000/sifter/getsummary/{Name of the document as is in the directory mentioned above}
	d. You should be able to see the results in JSON format on the browser.
	
2. From command-line (used only for testing and demonstration)
	a. Run "SummaryController.java" in the com.sifter.email.controller package.
	b. Follow instructions:
		1 is the training set, 2 is the test set, 0 is to exit.
	c. Enter the name of the document exactly in the concerned set and hit enter/return for results
