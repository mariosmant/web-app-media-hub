FROM apache/spark:4.1.1 AS sparkdist

FROM apache/airflow:3.1.6-python3.12

# Install OS-level dependencies (only what you really need)
USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       build-essential \
         openjdk-17-jre-headless \
       libpq-dev \
    && apt-get autoremove -yqq --purge \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY --from=sparkdist /opt/spark /opt/spark

ENV SPARK_HOME=/opt/spark
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="/opt/spark/bin:${PATH}"

# Drop back to non-root
USER airflow

# Install Python dependencies
COPY requirements.txt /requirements.txt
RUN pip install --no-cache-dir -r /requirements.txt

# (Optional) Embed DAGs and plugins into the image
# For local dev you can still override with volumes in docker-compose
COPY dags/ /opt/airflow/dags/
COPY dist/ /opt/airflow/dist/
COPY plugins/ /opt/airflow/plugins/
