USE dart_tv_v1;

SET hive.execution.engine=tez;
SET tez.session.client.timeout.secs=3600;
SET tez.session.am.dag.submit.timeout.secs=3600;
SET tez.queue.name=dart;

DROP TABLE IF EXISTS dart_av_act_top15_error_summary_chart;

CREATE TABLE IF
  NOT EXISTS dart_av_act_top15_error_summary_chart (
	sheet_name string,
    error_code string,
	error_count int,
	percent float,
    error_message string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\001'
STORED AS textfile
LOCATION '/db/e2e/dart_caap2/charts/dart_av_act_top15_error_summary_chart';