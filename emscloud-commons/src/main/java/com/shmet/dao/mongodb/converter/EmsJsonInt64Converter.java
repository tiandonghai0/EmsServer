package com.shmet.dao.mongodb.converter;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

public class EmsJsonInt64Converter implements Converter<Long> {
  @Override
  public void convert(final Long value, final StrictJsonWriter writer) {
      writer.writeString(Long.toString(value));
  }
}
