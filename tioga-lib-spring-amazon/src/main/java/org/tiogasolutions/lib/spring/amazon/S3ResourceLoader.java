/*
 * Copyright 2012 Jacob D Parr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tiogasolutions.lib.spring.amazon;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import java.io.InputStream;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.springframework.core.io.*;

public class S3ResourceLoader implements ResourceLoader {

  private static final String LOCATION_PREFIX = "s3://";

  public static class S3Path {
    public String bucket;
    public String key;
  }

  private AmazonS3 client;

  public S3ResourceLoader() {
    this.client = AwsUtils.newAmazonS3Client();
  }

  @Override
  public ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }

  @Override
  public Resource getResource(String location) {
    try {
      S3Path s3Path = parseS3Path(location);
      S3Object s3Object = client.getObject(s3Path.bucket, s3Path.key);

      InputStream inputStream = s3Object.getObjectContent();
      byte[] bytes = IoUtils.toBytes(inputStream);
      return new ByteArrayResource(bytes, location);

    } catch (Exception e) {
      throw new S3ResourceException("could not load resource from " + location, e);
    }
  }

  private S3Path parseS3Path(String location) {
    ExceptionUtils.assertNotZeroLength(location, "location");

    if (location.startsWith(LOCATION_PREFIX) == false) {
      String msg = String.format("AWS S3 resource locations must start with \"%s\": %s.", LOCATION_PREFIX, location);
      throw new IllegalArgumentException(msg);
    }

    S3Path s3Path = new S3Path();
    String path = location.substring(LOCATION_PREFIX.length());
    int pos = path.indexOf("/");

    if (pos < 0) {
      String msg = String.format("The path does not specify a bucket name: %s.", path);
      throw new IllegalArgumentException(msg);
    }

    s3Path.bucket = path.substring(0, pos);
    s3Path.key = path.substring(pos + 1, path.length());

    return s3Path;
  }
}