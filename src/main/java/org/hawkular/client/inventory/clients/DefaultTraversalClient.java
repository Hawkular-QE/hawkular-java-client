/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.client.inventory.clients;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.hawkular.client.core.BaseClient;
import org.hawkular.client.core.ClientInfo;
import org.hawkular.client.core.ClientResponse;
import org.hawkular.client.core.DefaultClientResponse;
import org.hawkular.client.core.jaxrs.ResponseCodes;
import org.hawkular.client.core.jaxrs.RestFactory;
import org.hawkular.client.inventory.jaxrs.handlers.TraversalHandler;
import org.hawkular.inventory.paths.CanonicalPath;

import com.fasterxml.jackson.databind.JavaType;

public class DefaultTraversalClient extends BaseClient<TraversalHandler> implements TraversalClient {

    public DefaultTraversalClient(ClientInfo clientInfo) {
        super(clientInfo, new RestFactory<TraversalHandler>(TraversalHandler.class));
    }

    @Override
    public ClientResponse<List<Map>> getTraversal(CanonicalPath traversal) {
        Response serverResponse = null;

        try {
            serverResponse = restApi().getTraversal(traversal.toRelativePath().toString());
            JavaType javaType = collectionResolver().get(List.class, Map.class);

            return new DefaultClientResponse<List<Map>>(javaType, serverResponse, ResponseCodes.GET_SUCCESS_200);
        } finally {
            if (serverResponse != null) {
                serverResponse.close();
            }
        }
    }
}