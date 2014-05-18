package org.tjumyk.metaview.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.tjumyk.metaview.Main;
import org.tjumyk.metaview.model.Category;
import org.tjumyk.metaview.model.Group;
import org.tjumyk.metaview.model.MetaVideo;

/**
 * Full-text search engine using Apache Lucene
 * 
 * @author 宇锴
 */
public class SearchEngine {
	/**
	 * Base path of the index folder used by Lucene
	 */
	private static final String INDEX_DIR_BASE = Main.TEMP_DIR + "index";

	/**
	 * Build indexes for {@link MetaVideo} model object
	 * 
	 * @param video
	 *            meta video model object
	 * @throws IOException
	 *             if IO error
	 */
	public static void indexModel(MetaVideo video) throws IOException {
		File indexDir = new File(INDEX_DIR_BASE
				+ video.getMovieFile().hashCode());
		if (!indexDir.exists())
			indexDir.mkdirs();

		Directory dir = FSDirectory.open(indexDir);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_48,
				analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, iwc);

		for (Category cat : video.getCategories()) {
			for (Group group : cat.getGroups()) {
				Document doc = new Document();
				doc.add(new TextField("name", group.getName(), Field.Store.YES));
				doc.add(new IntField("key", group.getKey(), Field.Store.YES));
				doc.add(new TextField("cat_name",
						group.getCategory().getName(), Field.Store.YES));
				doc.add(new TextField("info", Jsoup.parse(group.getInfo())
						.text(), Field.Store.NO));
				writer.addDocument(doc);
			}
		}
		writer.close();
	}

	/**
	 * Do a full-text search with the given key on the given model.
	 * {@link #indexModel(MetaVideo)} must be called with this model before
	 * doing any search on it.
	 * 
	 * @param video
	 *            meta video model object
	 * @param key
	 *            search key
	 * @return search results (list of related {@link Group}s)
	 * @throws IOException
	 *             if IO error
	 * @throws ParseException
	 *             if parse error
	 */
	public static List<Group> searchModel(MetaVideo video, String key)
			throws IOException, ParseException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
				INDEX_DIR_BASE + video.getMovieFile().hashCode())));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
		QueryParser parser = new QueryParser(Version.LUCENE_48, "info",
				analyzer);
		String queryStr = "name:\"" + key + "\" OR info:\"" + key + "\"";
		Query query = parser.parse(queryStr);
		TopDocs results = searcher.search(query, 8);
		ScoreDoc[] hits = results.scoreDocs;

		List<Group> list = new ArrayList<>();
		for (ScoreDoc hit : hits) {
			Document doc = searcher.doc(hit.doc);
			String groupName = doc.get("name");
			String catName = doc.get("cat_name");
			int groupKey = doc.getField("key").numericValue().intValue();
			for (Category cat : video.getCategories()) {
				if (cat.getName().equals(catName)) {
					for (Group group : cat.getGroups()) {
						if (group.getName().equals(groupName)
								&& group.getKey() == groupKey) {
							list.add(group);
							break;
						}
					}
					break;
				}
			}
		}
		return list;
	}
}
