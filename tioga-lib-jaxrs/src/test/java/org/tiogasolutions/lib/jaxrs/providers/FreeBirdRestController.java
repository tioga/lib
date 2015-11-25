package org.tiogasolutions.lib.jaxrs.providers;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.tiogasolutions.dev.common.*;
import org.tiogasolutions.dev.common.fine.*;
import org.tiogasolutions.dev.common.net.InetMediaType;
import org.tiogasolutions.dev.domain.locality.LatLng;
import org.tiogasolutions.dev.testing.domain.FreeBird;

@Path("/api")
public class FreeBirdRestController {

  private final Map<String,String> userEmailMap = BeanUtils.toMap("mickey:mickey.mouse@disney.com", "minnie:minnie.mouse@disney.com");

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response sayHello() {
    return Response.ok("Ya, I see you.").build();
  }

  @GET
  @Path("/date/{date}/month-of-year")
  @Produces(MediaType.APPLICATION_JSON)
  public int getCurrentDate(@PathParam("date") LocalDate date) {
    return date.getMonthValue();
  }

  @GET
  @Path("/date/{date}/dates-in-month")
  @Produces(MediaType.APPLICATION_JSON)
  public List<LocalDate> getDaysInMonth(@PathParam("date") LocalDate date) {
    LocalDate firstDate = date.with(TemporalAdjusters.firstDayOfMonth());
    int max = date.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
    List<LocalDate> dates = new ArrayList<>();

    for (int i = 0; i < max; i++) {
      dates.add(firstDate.plusDays(i));
    }

    return dates;
  }

  @GET
  @Path("/deprecated/{userName}/email")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getDeprecatedUsersEmail(@PathParam("userName") String userName) {
    if (userEmailMap.containsKey(userName)) {
      return Response.ok(userEmailMap.get(userName)).build();
    } else {
      return Response.status(404).entity("No email for " + userName).build();
    }
  }

  @GET
  @Path("/users/{userName}/email")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getUsersEmail(@PathParam("userName") String userName) {
    if (userEmailMap.containsKey(userName)) {
      return Response.ok(userEmailMap.get(userName)).build();
    } else {
      return Response.status(404).entity("No email for " + userName).build();
    }
  }

  @GET
  @Path("/plain-text")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public String getPlainText(@QueryParam("signature") String signature) {

    if (StringUtils.isBlank(signature)) {
      signature = "- The People of Tioga Pass";
    }

    return "This is plain text.\n" +
        "There really isn't much to it.\n\n" +
        "\t" +
        signature;
  }

  @GET
  @Path("/free-bird")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public FreeBird getFreeBird(@QueryParam("firstMessage") String firstMessage) {
    if (StringUtils.isBlank(firstMessage)) {
      firstMessage = "Hi, my name is Moe!";
    }
    return createFreeBird(firstMessage);
  }

  @POST
  @Path("/null-bird")
  @Produces(MediaType.APPLICATION_JSON)
  public FreeBird getNullBird() {
    return createFreeBird("This is a null bird.");
  }

  @POST
  @Path("/free-bird")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public FreeBird echoFreeBird(FreeBird freeBird) {
    return freeBird;
  }

  @GET
  @Path("/sample.pdf")
  @Produces(InetMediaType.APPLICATION_PDF_VALUE)
  public Response getSamplePdf() throws IOException {
    InputStream is = getClass().getResourceAsStream("/tioga-lib-jaxrs-jackson/sample.pdf");
    byte[] bytes = IoUtils.toBytes(is);
    return Response.ok(bytes).build();
  }

  private FreeBird createFreeBird(String firstMessage) {
    return new FreeBird(
        "string-value", Long.MAX_VALUE, Integer.MIN_VALUE,
        new LatLng("37.3382030", "-119.7085060"),
        new org.tiogasolutions.dev.domain.money.Money("19.95"),

        DateUtils.toLocalTime("12:32:13.111"),
        DateUtils.toLocalDate("1974-09-03"),
        DateUtils.toLocalDateTime("1975-05-06T10:32:13.222"),
        DateUtils.toZonedDateTime("1997-07-11T01:32:13.333-07:00[America/Los_Angeles]"),

        new TraitMap("first:yes", "empty-value:", "null-value", "last:ok"),
        FineMessage.withText("This message is fine!"),
        (FineMessageSetImpl) new FineMessageSetBuilder()
            .withText(firstMessage)
            .withAll("I'm Suzie", "999", new TraitMap("girl:true", "boy:false")).build());
  }
}
