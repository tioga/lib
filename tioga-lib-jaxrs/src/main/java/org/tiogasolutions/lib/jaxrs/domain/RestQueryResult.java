package org.tiogasolutions.lib.jaxrs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.tiogasolutions.dev.common.MapBuilder;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.dev.domain.query.QueryResult;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.*;

public class RestQueryResult<T> implements QueryResult<T> {

  private final Class<? extends T> containsType;
  private final int limit;
  private final int offset;
  private int totalFound;
  private boolean totalExact;
  private final List<T> results;

  private final Map<String,URI> links;

  private RestQueryResult(UriInfo uriInfo, String path,
                          Class<? extends T> containsType,
                          int limit,
                          int offset,
                          int totalFound,
                          boolean totalExact,
                          Collection<T> results) {

    this.containsType = containsType;
    this.limit = limit;
    this.offset = offset;
    this.totalFound = totalFound;
    this.totalExact = totalExact;

    ArrayList<T> list = new ArrayList<>(results);
    this.results = Collections.unmodifiableList(list);

    URI self = build(uriInfo, path, offset, limit);
    URI first;
    URI prev;
    URI next;
    URI last;

    if (results.isEmpty()) {
      first = null;
      prev = null;
      next = null;
      last = null;

    } else {
      // If we are not builder, the first is always index 0.
      first = build(uriInfo, path, 0, limit);

      // We cannot have a last page if the total is not exact.
      // When we have the last, put us one page from the end.
      last = (totalExact == false) ? null : build(uriInfo, path, totalFound-limit, limit);

      int prevPage = Math.min(Math.max(0, offset-limit), totalFound-1);
      prev = (offset == 0) ? null : build(uriInfo, path, prevPage, limit);

      int nextPage = offset+limit;
      next = (offset + results.size() >= totalFound) ? null : build(uriInfo, path, nextPage, limit);
    }

    this.links = new MapBuilder<>(String.class, URI.class)
        .put("self", self)
        .put("first", first)
        .put("prev", prev)
        .put("next", next)
        .put("last", last)
        .buildLinkedHashMap();
  }

  protected URI build(UriInfo uriInfo, String path, int offset, int limit) {
    UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
    return uriBuilder.path(path)
        .replaceQueryParam("offset", offset)
        .replaceQueryParam("limit", limit)
        .build();
  }

  public Map<String, URI> getLinks() {
    return links;
  }

  @Override
  public Class<? extends T> getContainsType() {
    return containsType;
  }

  @Override
  public int getLimit() {
    return limit;
  }

  @Override
  public int getOffset() {
    return offset;
  }

  @Override
  public int getTotalFound() {
    return totalFound;
  }

  @Override
  public boolean isTotalExact() {
    return totalExact;
  }

  @Override
  public List<T> getResults() {
    return results;
  }




  @Override
  @JsonIgnore
  public boolean isContainsType(Class<?> containsType) {
    return this.containsType.isAssignableFrom(containsType);
  }

  @Override
  @JsonIgnore
  public boolean isEmpty() {
    return results.isEmpty();
  }

  @Override
  @JsonIgnore
  public boolean isNotEmpty() {
    return !results.isEmpty();
  }

  @Override
  @JsonIgnore
  public int getSize() {
    return results.size();
  }

  @Override
  @JsonIgnore
  public boolean getHasPrevious() {
    return offset != 0;
  }

  @Override
  @JsonIgnore
  public boolean getHasNext() {
    return offset + results.size() < totalFound;
  }

  @Override
  @JsonIgnore
  public T getFirst() {
    return results.get(0);
  }

  @Override
  @JsonIgnore
  public T getLast() {
    return results.get(results.size() - 1);
  }

  @Override
  public T getAt(int index) {
    return results.get(index);
  }

  @Override
  public Iterator<T> iterator() {
    return results.iterator();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RestQueryResult<?> that = (RestQueryResult)o;

    if (limit != that.limit) return false;
    if (offset != that.offset) return false;
    if (totalFound != that.totalFound) return false;
    if (results.size() != that.results.size()) return false;
    if (!results.equals(that.results)) return false;
    if (!containsType.equals(that.containsType)) return false;
    if (!links.equals(that.links)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = containsType.hashCode();
    result = 31 * result + offset;
    result = 31 * result + limit;
    result = 31 * result + totalFound;
    result = 31 * result + results.hashCode();
    result = 31 * result + links.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "JaxRsQueryResult{" +
        "type=" + containsType +
        ", limit=" + limit +
        ", offset=" + offset +
        ", totalFound=" + totalFound +
        ", results=" + results +
        ", links=" + links +
        '}';
  }

  public static <T> RestQueryResult<T> from(UriInfo uriInfo, String path,
                                            QueryResult<T> queryResult) {
    return newResult(
      uriInfo, path,
      queryResult.getContainsType(),
      queryResult.getLimit(),
      queryResult.getOffset(),
      queryResult.getTotalFound(),
      queryResult.isTotalExact(),
      queryResult.getResults());
  }

  public static <T> RestQueryResult<T> newEmpty(UriInfo uriInfo, String path, Class<T> containsType) {

    return new RestQueryResult<>(
      uriInfo, path,
      containsType,
      0,    // limit
      0,    // offset
      0,    // total found
      true, // is exact
      Collections.<T>emptyList()
    );
  }

  public static <T> RestQueryResult<T> newSingle(UriInfo uriInfo, String path,
                                                 Class<T> containsType,
                                                 T result) {
    ExceptionUtils.assertNotNull(result, "result");

    return newResult(
        uriInfo, path,
        containsType,
        1,    // limit
        0,    // offset
        1,    // total found
        true, // is exact
        Collections.singleton(result));
  }

  @SafeVarargs
  public static <T> RestQueryResult<T> newComplete(UriInfo uriInfo, String path,
                                                   Class<T> containsType,
                                                   T...results) {
    return newComplete(
        uriInfo, path,
        containsType,
        Arrays.asList(results));
  }

  public static <T> RestQueryResult<T> newComplete(UriInfo uriInfo, String path,
                                                   Class<T> containsType,
                                                   Collection<T> results) {
    return newResult(
        uriInfo, path,
        containsType,
        results.size(), // limit
        0,              // offset
        results.size(), // total found
        true,           // is exact
        results);
  }

  @SafeVarargs
  public static <T> RestQueryResult<T> newResult(UriInfo uriInfo, String path,
                                                 Class<? extends T> containsType,
                                                 int limit,
                                                 int offset,
                                                 int totalFound,
                                                 boolean totalExact,
                                                 T...results) {
    return newResult(
        uriInfo, path,
        containsType,
        limit,
        offset,
        totalFound,
        totalExact,
        Arrays.asList(results));
  }

  public static <T> RestQueryResult<T> newResult(UriInfo uriInfo, String path,
                                                 Class<? extends T> containsType,
                                                 int limit,
                                                 int offset,
                                                 int totalFound,
                                                 boolean totalExact,
                                                 Collection<T> results) {
    return new RestQueryResult<>(
        uriInfo, path,
        containsType,
        limit,
        offset,
        totalFound,
        totalExact,
        results
    );
  }
}
