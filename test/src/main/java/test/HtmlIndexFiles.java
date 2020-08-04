package test;

import java.io.File;
import java.io.FileFilter;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.benchmark.byTask.feeds.DemoHTMLParser.Parser;

public class HtmlIndexFiles {



    public static void Result(Analyzer analyzer,String str) throws Exception{

        StringReader reader=new  StringReader(str);

        TokenStream tokenStream = analyzer.
                tokenStream("test", reader);
        tokenStream.reset();

        CharTermAttribute attr=
                tokenStream.getAttribute(CharTermAttribute.class);

        while(tokenStream.incrementToken()){
            System.out.println(attr.toString());
        }
    }
    public static List<File> searchFiles(File folder, String keyword) {
        List<File> result = new ArrayList<File>();
        if (folder.isFile())
            result.add(folder);

        File[] subFolders = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                if (file.getName().toLowerCase().contains(keyword)) {
                    return true;
                }
                return false;
            }
        });

        if (subFolders != null) {
            for (File file : subFolders) {
                if (file.isFile()) {

                    result.add(file);
                } else {

                    result.addAll(searchFiles(file, keyword));
                }
            }
        }
        return result;
    }
    public static void createIndex() throws Exception{
        //create the index file and put the Directory to the index file
        Directory indexDirectory = FSDirectory.open(Paths.get("D:\\Idea_project\\sfu_1_\\dirr")) ;

        Analyzer analyzer = new StandardAnalyzer() ;

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);

        File file=new File("D:\\Idea_project\\sfu_1_\\src\\en");
        String keyWord = "html";
        List<File> files=searchFiles(file,keyWord);
        for (File file1 : files) {
            StandardAnalyzer a1=new StandardAnalyzer();

            String file1Name=file1.getName();

            String path=file1.getPath();

            String fileContext = FileUtils.readFileToString(file1, "utf-8");

            Field fieldName=new TextField("name",file1Name, Field.Store.YES);
            Field fieldPath=new StoredField("path",path);
            Field fieldContext=new TextField("context",fileContext, Field.Store.YES);

            Document document=new Document();

            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldContext);

            indexWriter.addDocument(document);
            break;
        }

        indexWriter.close();
    }
    public static void searchIndex() throws Exception {

        Directory directory = FSDirectory.open(new File("D:\\Idea_project\\sfu_1_\\dirr").toPath());

        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = new TermQuery(new Term("context", "title"));

        TopDocs topDocs = indexSearcher.search(query, 7000);

        Analyzer a1 = new StandardAnalyzer();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {

            Document document = indexSearcher.doc(scoreDoc.doc);
            Parser parse = new Parser(new StringReader(document.get("context")));

            System.out.println(document.get("name"));
            String x = document.get("context");
            System.out.println("title:"+" "+x.substring(x.indexOf("<title>")+7,x.indexOf("</title>")));
            System.out.println("content: ");
            Result(a1,parse.body.toLowerCase());

        }

        indexReader.close();
    }

    public static void main(String[] args) throws Exception{

        createIndex();
        searchIndex();

    }
}