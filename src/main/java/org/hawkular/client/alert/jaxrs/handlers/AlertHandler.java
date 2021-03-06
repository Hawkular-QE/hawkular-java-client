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
package org.hawkular.client.alert.jaxrs.handlers;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hawkular.alerts.api.model.data.Data;

/**
 * Alerts API
 * http://www.hawkular.org/docs/rest/rest-alerts.html#_
 */
@Path("/hawkular/alerts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AlertHandler {

    @GET
    @Path("/")
    Response findAlerts(
        @QueryParam("startTime") final Long startTime,
        @QueryParam("endTime") final Long endTime,
        @QueryParam("alertIds") final String alertIds,
        @QueryParam("triggerIds") final String triggerIds,
        @QueryParam("statuses") final String statuses,
        @QueryParam("severities") final String severities,
        @QueryParam("tags") final String tags,
        @QueryParam("thin") final Boolean thin);

    @PUT
    @Path("/ack")
    Response ackAlerts(
        @QueryParam("alertIds") final String alertIds,
        @QueryParam("ackBy") final String ackBy,
        @QueryParam("ackNotes") final String ackNotes);

    @PUT
    @Path("/ack/{alertId}")
    Response ackAlert(
        @PathParam("alertId") final String alertId,
        @QueryParam("ackBy") final String ackBy,
        @QueryParam("ackNotes") final String ackNotes);

    @GET
    @Path("/alert/{alertId}")
    Response getAlert(
        @PathParam("alertId") final String alertId,
        @QueryParam("thin") final Boolean thin);

    @POST
    @Path("/data")
    Response sendData(final List<Data> datums);

    @PUT
    @Path("/delete")
    Response deleteAlerts(
        @QueryParam("startTime") final Long startTime,
        @QueryParam("endTime") final Long endTime,
        @QueryParam("alertIds") final String alertIds,
        @QueryParam("triggerIds") final String triggerIds,
        @QueryParam("statuses") final String statuses,
        @QueryParam("severities") final String severities,
        @QueryParam("tags") final String tags);

    @PUT
    @Path("/note/{alertId}")
    Response addNoteToAlert(@PathParam("alertId") final String alertId,
                          @QueryParam("user") final String user,
                          @QueryParam("text") final String text);

    @PUT
    @Path("/resolve")
    Response resolveAlerts(
        @QueryParam("alertIds") final String alertIds,
        @QueryParam("resolvedBy") final String resolvedBy,
        @QueryParam("resolvedNotes") final String resolvedNotes);

    @PUT
    @Path("/resolve/{alertId}")
    Response resolveAlert(
        @PathParam("alertId") final String alertId,
        @QueryParam("resolvedBy") final String resolvedBy,
        @QueryParam("resolvedNotes") final String resolvedNotes);

    @DELETE
    @Path("/tags")
    Response deleteTags(@QueryParam("alertIds") final String alertIds,
                        @QueryParam("tagNames") final String tagNames);

    @PUT
    @Path("/tags")
    Response addTag(@QueryParam("alertIds") final String alertIds,
                    @QueryParam("tags") final String tagNames);

    @DELETE
    @Path("/{alertId}")
    Response deleteAlert(@PathParam("alertId") final String alertId);
}
