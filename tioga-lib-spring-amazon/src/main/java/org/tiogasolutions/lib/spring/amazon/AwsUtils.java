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

import com.amazonaws.auth.*;
import com.amazonaws.services.s3.AmazonS3Client;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.dev.common.StringUtils;

public class AwsUtils {

  private static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
  private static final String AWS_SECRET_KEY = "AWS_SECRET_KEY";

  public static AWSCredentials getCredentials() {
    return new BasicAWSCredentials(getAccessKey(), getSecretKey());
  }

  public static String getAccessKey() {
    return EnvUtils.requireProperty(AWS_ACCESS_KEY_ID);
  }

  public static String getSecretKey() {
    return EnvUtils.requireProperty(AWS_SECRET_KEY);
  }

  public static AmazonS3Client newAmazonS3Client() {
    return new AmazonS3Client(getCredentials());
  }
}
