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

package com.abecerra.gnssanalysis.core.suplclient.asn1.supl2.lpp;

// Copyright 2008 Google Inc. All Rights Reserved.
/*
 * AUTOMATICALLY GENERATED. Do NOT EDIT.
 */


//
//
import com.abecerra.gnssanalysis.core.suplclient.asn1.base.Asn1Enumerated;
import com.abecerra.gnssanalysis.core.suplclient.asn1.base.Asn1Tag;
import com.abecerra.gnssanalysis.core.suplclient.asn1.base.BitStream;
import com.abecerra.gnssanalysis.core.suplclient.asn1.base.BitStreamReader;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import javax.annotation.Nullable;


/**
 * 
 */
public  class LocationInformationType extends Asn1Enumerated {
  public enum Value implements Asn1Enumerated.Value {
    locationEstimateRequired(0),
    locationMeasurementsRequired(1),
    locationEstimatePreferred(2),
    locationMeasurementsPreferred(3),
    ;

    Value(int i) {
      value = i;
    }

    private int value;
    public int getAssignedValue() {
      return value;
    }

    @Override public boolean isExtensionValue() {
      return false;
    }
  }

  @Override
  protected Value getDefaultValue() {
    return null
;
  }

  @SuppressWarnings("unchecked")
  public Value enumValue() {
    return (Value) getValue();
  }


  
  public void setTo_locationEstimateRequired() {
    setValue(Value.locationEstimateRequired);
  }
  
  public void setTo_locationMeasurementsRequired() {
    setValue(Value.locationMeasurementsRequired);
  }
  
  public void setTo_locationEstimatePreferred() {
    setValue(Value.locationEstimatePreferred);
  }
  
  public void setTo_locationMeasurementsPreferred() {
    setValue(Value.locationMeasurementsPreferred);
  }
  


  public enum ExtensionValue implements Asn1Enumerated.Value {
    ;

    ExtensionValue(int i) {
      value = i;
    }

    private int value;
    @Override public int getAssignedValue() {
      return value;
    }

    @Override public boolean isExtensionValue() {
      return true;
    }
  }

  @SuppressWarnings("unchecked")
  public ExtensionValue extEnumValue() {
    return (ExtensionValue) getValue();
  }

  

  

  private static final Asn1Tag TAG_LocationInformationType
      = Asn1Tag.fromClassAndNumber(-1, -1);

  public LocationInformationType() {
    super();
    // use template substitution instead of calling getDefaultValue(), since
    // calling virtual methods from a ctor is frowned upon here.
    setValue(null
);
  }

  @Override
  @Nullable
  protected Asn1Tag getTag() {
    return TAG_LocationInformationType;
  }

  @Override
  protected boolean isTagImplicit() {
    return true;
  }

  public static Collection<Asn1Tag> getPossibleFirstTags() {
    if (TAG_LocationInformationType != null) {
      return ImmutableList.of(TAG_LocationInformationType);
    } else {
      return Asn1Enumerated.getPossibleFirstTags();
    }
  }

  @Override protected boolean isExtensible() {
    return true;
  }

  @Override protected Asn1Enumerated.Value lookupValue(int ordinal) {
    return Value.values()[ordinal];
  }

  @Override protected Asn1Enumerated.Value lookupExtensionValue(int ordinal) {
    return ExtensionValue.values()[ordinal];
  }

  @Override protected int getValueCount() {
    return Value.values().length;
  }

  /**
   * Creates a new LocationInformationType from encoded stream.
   */
  public static LocationInformationType fromPerUnaligned(byte[] encodedBytes) {
    LocationInformationType result = new LocationInformationType();
    result.decodePerUnaligned(new BitStreamReader(encodedBytes));
    return result;
  }

  /**
   * Creates a new LocationInformationType from encoded stream.
   */
  public static LocationInformationType fromPerAligned(byte[] encodedBytes) {
    LocationInformationType result = new LocationInformationType();
    result.decodePerAligned(new BitStreamReader(encodedBytes));
    return result;
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
    return "LocationInformationType = " + getValue() + ";\n";
  }
}
