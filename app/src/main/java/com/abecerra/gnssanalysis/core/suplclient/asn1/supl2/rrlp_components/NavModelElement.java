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

package com.abecerra.gnssanalysis.core.suplclient.asn1.supl2.rrlp_components;

// Copyright 2008 Google Inc. All Rights Reserved.
/*
 * This class is AUTOMATICALLY GENERATED. Do NOT EDIT.
 */


//
//
import com.abecerra.gnssanalysis.core.suplclient.asn1.base.Asn1Object;
import com.abecerra.gnssanalysis.core.suplclient.asn1.base.Asn1Sequence;
import com.abecerra.gnssanalysis.core.suplclient.asn1.base.Asn1Tag;
import com.abecerra.gnssanalysis.core.suplclient.asn1.base.BitStream;
import com.abecerra.gnssanalysis.core.suplclient.asn1.base.BitStreamReader;
import com.abecerra.gnssanalysis.core.suplclient.asn1.base.SequenceComponent;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import javax.annotation.Nullable;


/**
* 
*/
public  class NavModelElement extends Asn1Sequence {
  //

  private static final Asn1Tag TAG_NavModelElement
      = Asn1Tag.fromClassAndNumber(-1, -1);

  public NavModelElement() {
    super();
  }

  @Override
  @Nullable
  protected Asn1Tag getTag() {
    return TAG_NavModelElement;
  }

  @Override
  protected boolean isTagImplicit() {
    return true;
  }

  public static Collection<Asn1Tag> getPossibleFirstTags() {
    if (TAG_NavModelElement != null) {
      return ImmutableList.of(TAG_NavModelElement);
    } else {
      return Asn1Sequence.getPossibleFirstTags();
    }
  }

  /**
   * Creates a new NavModelElement from encoded stream.
   */
  public static NavModelElement fromPerUnaligned(byte[] encodedBytes) {
    NavModelElement result = new NavModelElement();
    result.decodePerUnaligned(new BitStreamReader(encodedBytes));
    return result;
  }

  /**
   * Creates a new NavModelElement from encoded stream.
   */
  public static NavModelElement fromPerAligned(byte[] encodedBytes) {
    NavModelElement result = new NavModelElement();
    result.decodePerAligned(new BitStreamReader(encodedBytes));
    return result;
  }



  @Override protected boolean isExtensible() {
    return false;
  }

  @Override public boolean containsExtensionValues() {
    for (SequenceComponent extensionComponent : getExtensionComponents()) {
      if (extensionComponent.isExplicitlySet()) return true;
    }
    return false;
  }

  
  private SatelliteID satelliteID_;
  public SatelliteID getSatelliteID() {
    return satelliteID_;
  }
  /**
   * @throws ClassCastException if value is not a SatelliteID
   */
  public void setSatelliteID(Asn1Object value) {
    this.satelliteID_ = (SatelliteID) value;
  }
  public SatelliteID setSatelliteIDToNewInstance() {
    satelliteID_ = new SatelliteID();
    return satelliteID_;
  }
  
  private SatStatus satStatus_;
  public SatStatus getSatStatus() {
    return satStatus_;
  }
  /**
   * @throws ClassCastException if value is not a SatStatus
   */
  public void setSatStatus(Asn1Object value) {
    this.satStatus_ = (SatStatus) value;
  }
  public SatStatus setSatStatusToNewInstance() {
    satStatus_ = new SatStatus();
    return satStatus_;
  }
  

  

  

  @Override public Iterable<? extends SequenceComponent> getComponents() {
    ImmutableList.Builder<SequenceComponent> builder = ImmutableList.builder();
    
    builder.add(new SequenceComponent() {
          Asn1Tag tag = Asn1Tag.fromClassAndNumber(2, 0);

          @Override public boolean isExplicitlySet() {
            return getSatelliteID() != null;
          }

          @Override public boolean hasDefaultValue() {
            return false;
          }

          @Override public boolean isOptional() {
            return false;
          }

          @Override public Asn1Object getComponentValue() {
            return getSatelliteID();
          }

          @Override public void setToNewInstance() {
            setSatelliteIDToNewInstance();
          }

          @Override public Collection<Asn1Tag> getPossibleFirstTags() {
            return tag == null ? SatelliteID.getPossibleFirstTags() : ImmutableList.of(tag);
          }

          @Override
          public Asn1Tag getTag() {
            return tag;
          }

          @Override
          public boolean isImplicitTagging() {
            return true;
          }

          @Override public String toIndentedString(String indent) {
                return "satelliteID : "
                    + getSatelliteID().toIndentedString(indent);
              }
        });
    
    builder.add(new SequenceComponent() {
          Asn1Tag tag = Asn1Tag.fromClassAndNumber(2, 1);

          @Override public boolean isExplicitlySet() {
            return getSatStatus() != null;
          }

          @Override public boolean hasDefaultValue() {
            return false;
          }

          @Override public boolean isOptional() {
            return false;
          }

          @Override public Asn1Object getComponentValue() {
            return getSatStatus();
          }

          @Override public void setToNewInstance() {
            setSatStatusToNewInstance();
          }

          @Override public Collection<Asn1Tag> getPossibleFirstTags() {
            return tag == null ? SatStatus.getPossibleFirstTags() : ImmutableList.of(tag);
          }

          @Override
          public Asn1Tag getTag() {
            return tag;
          }

          @Override
          public boolean isImplicitTagging() {
            return true;
          }

          @Override public String toIndentedString(String indent) {
                return "satStatus : "
                    + getSatStatus().toIndentedString(indent);
              }
        });
    
    return builder.build();
  }

  @Override public Iterable<? extends SequenceComponent>
                                                    getExtensionComponents() {
    ImmutableList.Builder<SequenceComponent> builder = ImmutableList.builder();
      
      return builder.build();
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
    builder.append("NavModelElement = {\n");
    final String internalIndent = indent + "  ";
    for (SequenceComponent component : getComponents()) {
      if (component.isExplicitlySet()) {
        builder.append(internalIndent)
            .append(component.toIndentedString(internalIndent));
      }
    }
    if (isExtensible()) {
      builder.append(internalIndent).append("...\n");
      for (SequenceComponent component : getExtensionComponents()) {
        if (component.isExplicitlySet()) {
          builder.append(internalIndent)
              .append(component.toIndentedString(internalIndent));
        }
      }
    }
    builder.append(indent).append("};\n");
    return builder.toString();
  }
}
