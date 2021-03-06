package org.apache.kafka.connect.elasticsearchschema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;

/**
 * ElasticsearchSinkConnector implement the Connector interface to send Kafka data to Elasticsearch.
 *
 * @author Andrea Patelli
 */
public class ElasticsearchSinkConnector extends SinkConnector {
  public static final String CLUSTER_NAME = "elasticsearch.cluster.name";
  public static final String HOSTS = "elasticsearch.hosts";
  public static final String BULK_SIZE = "elasticsearch.bulk.size";
  public static final String INDEXES = "elasticsearch.indexes";
  public static final String DOCUMENT_NAME = "elasticsearch.document.name";
  public static final String TOPICS = "topics";
  public static final String DATE_FORMAT = "date.format";
  public static final String SUFFIX_SEPARATOR = "suffix.separator";

  private String clusterName;
  private String hosts;
  private String bulkSize;
  private String documentName;
  private String topics;
  private String indexes;
  private String dateFormat;
  private String suffixSeparator;

  /**
   * Get the version of this connector.
   *
   * @return the version, formatted as a String
   */
  @Override
  public String version() {
    return AppInfoParser.getVersion();
  }

  /**
   * Start this Connector. This method will only be called on a clean Connector, i.e. it has either just been
   * instantiated and initialized or {@link #stop()} has been invoked.
   *
   * @param props
   *          configuration settings
   */
  @Override
  public void start(Map<String, String> props) {
    clusterName = props.get(CLUSTER_NAME);
    hosts = props.get(HOSTS);
    bulkSize = props.get(BULK_SIZE);
    documentName = props.get(DOCUMENT_NAME);
    suffixSeparator = props.get(SUFFIX_SEPARATOR);
    if (clusterName == null || clusterName.isEmpty()) {
      throw new ConnectException(
          "ElasticsearchSinkConnector configuration must include 'elasticsearch.cluster.name' setting");
    }
    if (hosts == null || hosts.isEmpty()) {
      throw new ConnectException("ElasticsearchSinkConnector configuration must include 'elasticserch.hosts' setting");
    }
    if (bulkSize == null || bulkSize.isEmpty()) {
      throw new ConnectException(
          "ElasticsearchSinkConnector configuration must include 'elasticsearch.bulk.size' setting");
    }
    if (documentName == null) {
      documentName = "";
    }

    topics = props.get(TOPICS);
    indexes = props.get(INDEXES);
    if (topics == null || topics.isEmpty()) {
      throw new ConnectException("ElasticsearchSinkConnector configuration must include 'topics' setting");
    }
    if (indexes == null || indexes.isEmpty()) {
      throw new ConnectException(
          "ElasticsearchSinkConnector configuration must include 'elasticsearch.indexes' setting");
    }

    if (suffixSeparator == null) {
      suffixSeparator = "-";
    }
    dateFormat = props.get(DATE_FORMAT);
  }

  /**
   * Returns the Task implementation for this Connector.
   */
  @Override
  public Class<? extends Task> taskClass() {
    return ElasticsearchSinkTask.class;
  }

  /**
   * Returns a set of configurations for Tasks based on the current configuration, producing at most count
   * configurations.
   *
   * @param maxTasks
   *          maximum number of configurations to generate
   * @return configurations for Tasks
   */
  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    ArrayList<Map<String, String>> configs = new ArrayList<Map<String, String>>();
    for (int i = 0; i < maxTasks; i++) {
      Map<String, String> config = new HashMap<String, String>();
      config.put(CLUSTER_NAME, clusterName);
      config.put(HOSTS, hosts);
      config.put(BULK_SIZE, bulkSize);
      config.put(DOCUMENT_NAME, documentName);
      config.put(INDEXES, indexes);
      config.put(TOPICS, topics);
      // config.put("key.serializer.class", "io.confluent.kafka.serializers.KafkaAvroSerializer");
      // config.put("value.serializer.class", "io.confluent.kafka.serializers.KafkaAvroSerializer");
      if (dateFormat != null) {
        config.put(DATE_FORMAT, dateFormat);
      }
      config.put(SUFFIX_SEPARATOR, suffixSeparator);
      configs.add(config);
    }
    return configs;
  }

  /**
   * Stop this connector.
   */
  @Override
  public void stop() {
    // Nothing to do
  }

  @Override
  public ConfigDef config() {
    // TODO Auto-generated method stub
    return null;
  }
}
