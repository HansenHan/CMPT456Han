package test;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.benchmark.byTask.feeds.DemoHTMLParser.Parser;

import java.io.File;
import java.io.StringReader;
import java.util.Scanner;

public class SimpleMetrics {
    public static void searchIndex(String str) throws Exception {
        Directory directory = FSDirectory.open(new File("D:\\Idea_project\\sfu_1_\\dirr").toPath());

        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = new TermQuery(new Term("context", str));

        TopDocs topDocs = indexSearcher.search(query, 7000);

        System.out.println("Document Frequency："+ topDocs.totalHits);
        int Term_Frequency = 0;
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {

            Document document = indexSearcher.doc(scoreDoc.doc);
            Parser parse = new Parser(new StringReader(document.get("context")));
            Term_Frequency=countStr(parse.toString(),str);
            System.out.println("Term_Frequency: "+Term_Frequency);



        }

        indexReader.close();
    }

    public static int countStr(String str1, String str2){
        int counter =0;
        if (str1.indexOf(str2) == -1) {
            return 0;
        } else if (str1.indexOf(str2) != -1) {
            counter++;
            countStr(str1.substring(str1.indexOf(str2) +
                    str2.length()), str2);
            return counter;
        }
        return 0;
    }

    public static void main(String[] args) throws Exception{

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the term：");
        String name = sc.nextLine();
        searchIndex(name);



    }
}
