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
package org.hawkular.client.inventory.jaxrs.handlers;

import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Traversal API
 * http://www.hawkular.org/docs/rest/rest-inventory.html#_entity_graph_traversal
 */
@Path("/hawkular/inventory/traversal")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface TraversalHandler {

    @GET
    @Path("/{traversal}")
    Response getTraversal(@Encoded @PathParam("traversal") String traversal, @QueryParam("at") String at,
                          @QueryParam("page") Integer page, @QueryParam("per_page") Integer per_page,
                          @QueryParam("sort") String sort, @QueryParam("order") String order);
}
