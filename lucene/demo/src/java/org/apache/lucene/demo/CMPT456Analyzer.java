package test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.benchmark.byTask.feeds.DemoHTMLParser.Parser;
import org.apache.lucene.analysis.en.PorterStemFilter;

public class CMPT456Analyzer {


    public static void testAnalyzer(String str) throws IOException {
        Analyzer analyzer = new Analyzer() {
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tokenizer = new WhitespaceTokenizer();  // 空格分词
                TokenStream ts = new LowerCaseFilter(tokenizer);  // 转小写
                ts = new PorterStemFilter(ts);  // 词干提取
                return new TokenStreamComponents(tokenizer, ts);
            }
        };
        TokenStream ts = analyzer.tokenStream("", str);
        CharTermAttribute charTermAtt = ts.getAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            String term = charTermAtt.toString();
            System.out.println(term); //
        }
    }
    public static void Result(String str) throws Exception{

        StringBuffer sb = new StringBuffer();

        Path path = Paths.get("D:\\Idea_project\\test\\src\\main\\java\\test\\stopwords.txt");

        Analyzer analyzer1=new StopAnalyzer( path);

        StringReader reader=new  StringReader(str);

        TokenStream tokenStream = analyzer1.
                tokenStream("test", reader);
        tokenStream.reset();

        CharTermAttribute attr=
                tokenStream.getAttribute(CharTermAttribute.class);

        while(tokenStream.incrementToken()){
           sb.append(" "+attr.toString());
        }
        testAnalyzer(sb.toString());
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
//    public static void createIndex() throws Exception{
//
//        Directory indexDirectory = FSDirectory.open(Paths.get("D:\\Idea_project\\sfu_1_\\dirr")) ;
//
//        Analyzer analyzer = new StandardAnalyzer() ;
//
//        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
//        IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);
//
//        File file=new File("D:\\Idea_project\\sfu_1_\\src\\en");
//        String keyWord = "html";
//        List<File> files=searchFiles(file,keyWord);
//        for (File file1 : files) {
//            StandardAnalyzer a1=new StandardAnalyzer();
//            //取文件名
//            String file1Name=file1.getName();
//            //文件的路径
//            String path=file1.getPath();
//            //文件的内容
//            String fileContext = FileUtils.readFileToString(file1, "utf-8");
//
//            //创建Field
//            //参数1:域的名称,参数2：域的内容，参数3：是否储存
//            Field fieldName=new TextField("name",file1Name, Field.Store.YES);
//            Field fieldPath=new StoredField("path",path);
//            Field fieldContext=new TextField("context",fileContext, Field.Store.YES);
//            //创建文档对象
//            Document document=new Document();
////        4.向文档对象中添加域
//            document.add(fieldName);
//            document.add(fieldPath);
//            document.add(fieldContext);
////        5.把文档对象写入索引库
//            indexWriter.addDocument(document);
//        }
////        6.关闭indexWriter对象
//        indexWriter.close();
//    }
    public static void searchIndex() throws Exception {
        Directory directory = FSDirectory.open(new File("D:\\Idea_project\\sfu_1_\\dirr").toPath());


        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query  query = new TermQuery(new Term("context", "title"));

        TopDocs topDocs = indexSearcher.search(query, 7000);

        System.out.println("Document Frequency："+ topDocs.totalHits);


        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {

            Document document = indexSearcher.doc(scoreDoc.doc);
            Parser parse = new Parser(new StringReader(document.get("context")));

            System.out.println(document.get("name"));
            String x = document.get("context");
            System.out.println("title:"+" "+x.substring(x.indexOf("<title>")+7,x.indexOf("</title>")));
            System.out.println("content: ");
            Result(parse.body.toLowerCase());

        }
        indexReader.close();
    }




    public static void main(String[] args) throws Exception{
            searchIndex();
    }
}