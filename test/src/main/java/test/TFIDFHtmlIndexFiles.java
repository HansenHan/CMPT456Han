package test;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class TFIDFHtmlIndexFiles extends CMPT456Similarity{

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
                    // 如果是文件则将文件添加到结果列表中
                    result.add(file);
                } else {
                    // 如果是文件夹，则递归调用本方法，然后把所有的文件加到结果列表中
                    result.addAll(searchFiles(file, keyword));
                }
            }
        }
        return result;
    }
    public static void createIndex() throws Exception{
//        1.创建一个Director对象,指定索引库的位置。

        //把索引保存在内存中
        //把索引保存在磁盘中
        //读取需要索引的文件到Lucene的目录类中，新版的Lucene只支持IO2中的Path类型的变量了。
        Directory indexDirectory = FSDirectory.open(Paths.get("D:\\Idea_project\\sfu_1_\\dirr")) ;
        //创建分词器，这个分词器必须和IndexSearcher中的一致。
        Analyzer analyzer = new StandardAnalyzer() ;
        //新版的Lucene中索引创建类只接收IndexWriterConfig配置。
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);
//        3.读取磁盘上的文件，对应每个文件创建一个文档对象。
        File file=new File("D:\\Idea_project\\sfu_1_\\src\\en");
        String keyWord = "html";
        List<File> files=searchFiles(file,keyWord);
        for (File file1 : files) {
            StandardAnalyzer a1=new StandardAnalyzer();
            //取文件名
            String file1Name=file1.getName();
            //文件的路径
            String path=file1.getPath();
            //文件的内容
            String fileContext = FileUtils.readFileToString(file1, "utf-8");

            //创建Field
            //参数1:域的名称,参数2：域的内容，参数3：是否储存
            Field fieldName=new TextField("name",file1Name, Field.Store.YES);
            Field fieldPath=new StoredField("path",path);
            Field fieldContext=new TextField("context",fileContext, Field.Store.YES);
            //创建文档对象
            Document document=new Document();
//        4.向文档对象中添加域
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldContext);
//        5.把文档对象写入索引库
            indexWriter.addDocument(document);
            break;
        }
//        6.关闭indexWriter对象
        indexWriter.close();
    }


    public static void main(String[] args) throws Exception{

        createIndex();
    }
}