package test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.util.Scanner;

public class TFIDFSearchFiles extends CMPT456Similarity {

    public static void searchIndex(String str) throws Exception {

        Directory directory = FSDirectory.open(new File("D:\\Idea_project\\sfu_1_\\dirr").toPath());


        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        indexSearcher.setSimilarity(new CMPT456Similarity());

        Query query = new TermQuery(new Term("context", str));

        TopDocs topDocs = indexSearcher.search(query, 7000);

        Analyzer a1 = new StandardAnalyzer();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {

            Document document = indexSearcher.doc(scoreDoc.doc);

            System.out.println(document.get("name"));
            String x = document.get("context");
            System.out.println("title:"+" "+x.substring(x.indexOf("<title>")+7,x.indexOf("</title>")));
            System.out.println("content: ");


        }

        indexReader.close();
    }
    public static void main(String[] args) throws Exception{

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the term：");
        String name = sc.nextLine();
        searchIndex(name);
    }
}
