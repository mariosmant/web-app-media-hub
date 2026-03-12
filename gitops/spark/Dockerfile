FROM apache/spark:4.1.1

USER root

# Add project-provided Spark dependencies.
COPY ./spark/jars/ /opt/spark/jars/

# Add project Spark defaults used by master/worker.
COPY ./spark/conf/spark-defaults.conf /opt/spark/conf/spark-defaults.conf

RUN chown -R spark:spark /opt/spark/jars /opt/spark/conf/spark-defaults.conf \
    && chmod 644 /opt/spark/conf/spark-defaults.conf

USER spark
