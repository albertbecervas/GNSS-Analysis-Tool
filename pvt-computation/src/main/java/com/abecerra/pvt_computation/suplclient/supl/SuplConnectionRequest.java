// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.abecerra.pvt_computation.suplclient.supl;


/**
 * Holds SUPL Connection request parameters.
 */
public class SuplConnectionRequest {

    private String serverHost;
    private int serverPort;
    private boolean isSslEnabled = false;
    private boolean isLoggingEnabled = false;
    private boolean isMessageLoggingEnabled = false;

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerHost(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setSslEnabled(boolean isSslEnabled) {
        this.isSslEnabled = isSslEnabled;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        isLoggingEnabled = loggingEnabled;
    }

    public void setMessageLoggingEnabled(boolean messageLoggingEnabled) {
        isMessageLoggingEnabled = messageLoggingEnabled;
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public boolean isSslEnabled() {
        return isSslEnabled;
    }

    public boolean isLoggingEnabled() {
        return isLoggingEnabled;
    }

    public boolean isMessageLoggingEnabled() {
        return isMessageLoggingEnabled;
    }

}
