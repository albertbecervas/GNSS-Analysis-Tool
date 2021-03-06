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

package com.abecerra.pvt_ephemeris_client.suplclient.asn1.supl2.lpp;

// Copyright 2008 Google Inc. All Rights Reserved.
/*
 * This class is AUTOMATICALLY GENERATED. Do NOT EDIT.
 */


//
//
import com.abecerra.pvt_ephemeris_client.suplclient.asn1.base.Asn1SequenceOf;
import com.abecerra.pvt_ephemeris_client.suplclient.asn1.base.Asn1Tag;
import com.abecerra.pvt_ephemeris_client.suplclient.asn1.base.BitStream;
import com.abecerra.pvt_ephemeris_client.suplclient.asn1.base.BitStreamReader;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import javax.annotation.Nullable;


/**
 * 
 */
public  class Polygon
    extends Asn1SequenceOf<PolygonPoints> {
  //

  private static final Asn1Tag TAG_Polygon
      = Asn1Tag.fromClassAndNumber(-1, -1);

  public Polygon() {
    super();
    setMinSize(3);
setMaxSize(15);

  }

  @Override
  @Nullable
  protected Asn1Tag getTag() {
    return TAG_Polygon;
  }

  @Override
  protected boolean isTagImplicit() {
    return true;
  }

  public static Collection<Asn1Tag> getPossibleFirstTags() {
    if (TAG_Polygon != null) {
      return ImmutableList.of(TAG_Polygon);
    } else {
      return Asn1SequenceOf.getPossibleFirstTags();
    }
  }

  /**
   * Creates a new Polygon from encoded stream.
   */
  public static Polygon fromPerUnaligned(byte[] encodedBytes) {
    Polygon result = new Polygon();
    result.decodePerUnaligned(new BitStreamReader(encodedBytes));
    return result;
  }

  /**
   * Creates a new Polygon from encoded stream.
   */
  public static Polygon fromPerAligned(byte[] encodedBytes) {
    Polygon result = new Polygon();
    result.decodePerAligned(new BitStreamReader(encodedBytes));
    return result;
  }

  
  @Override public PolygonPoints createAndAddValue() {
    PolygonPoints value = new PolygonPoints();
    add(value);
    return value;
  }

  

  @Override public Iterable<BitStream> encodePerUnaligned() {
    return super.encodePerUnaligned();
  }

  @Override public Iterable<BitStream> encodePerAligned() {
    return super.encodePerAligned();
  }

  @Override public void decodePerUnaligned(BitStreamReader reader) {
    super.decodePerUnaligned(reader);
  }

  @Override public void decodePerAligned(BitStreamReader reader) {
    super.decodePerAligned(reader);
  }

  @Override public String toString() {
    return toIndentedString("");
  }

  public String toIndentedString(String indent) {
    StringBuilder builder = new StringBuilder();
    builder.append("Polygon = [\n");
    final String internalIndent = indent + "  ";
    for (PolygonPoints value : getValues()) {
      builder.append(internalIndent)
          .append(value.toIndentedString(internalIndent));
    }
    builder.append(indent).append("];\n");
    return builder.toString();
  }
}
