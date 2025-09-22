(ns maksut.files.s3-client
  (:require [com.stuartsierra.component :as component])
  (:import (software.amazon.awssdk.auth.credentials AwsCredentialsProvider DefaultCredentialsProvider ProfileCredentialsProvider)
           (software.amazon.awssdk.regions Region)
           (software.amazon.awssdk.services.s3 S3Client)))

(defn- credentials-provider ^AwsCredentialsProvider [config]
  (if-let [profile-name (get-in config [:file-store :s3 :credentials-profile])]
    (ProfileCredentialsProvider/create profile-name)
    (.build (DefaultCredentialsProvider/builder))))

(defn- region [config]
  (Region/of (get-in config [:file-store :s3 :region])))

(defrecord AwsS3Client [config]
  component/Lifecycle

  (start [this]
    (if (nil? (:s3-client this))
      (assoc this :s3-client (-> (S3Client/builder)
                                 (.region (region config))
                                 (.credentialsProvider (credentials-provider config))
                                 (.build)))
      this))

  (stop [this]
    (assoc this :s3-client nil)))

(defn new-client []
  (map->AwsS3Client {}))
