package org.vaadin.addons.componentfactory.schedulexcalendar.util;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
public class Resource implements Serializable {

  private String label;
  
  private String labelHtml;
  
  private String id;
    
  private Calendar calendar;  
  
  private List<Resource> resources = new ArrayList<Resource>();
  
  private boolean isOpen = false;
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Resource other = (Resource) obj;
    return Objects.equals(id, other.id);
  }
  
  public String getJson() {
    JsonObject js = Json.createObject();
    js.put("id", id);
    Optional.ofNullable(label).ifPresent(value -> js.put("label", value));
    Optional.ofNullable(labelHtml).ifPresent(value -> js.put("labelHTML", value));
    
    if(calendar != null) {
      Optional.ofNullable(calendar.getColorName()).ifPresent(value -> js.put("colorName", value));
      Optional.ofNullable(calendar.getLightColors()).ifPresent(colors -> js.put("lightColors", colors.toJsonObject()));
      Optional.ofNullable(calendar.getDarkColors()).ifPresent(colors -> js.put("darkColors", colors.toJsonObject()));
    }
    
    if (resources != null && !resources.isEmpty()) {
      JsonArray jsonResources = Json.createArray();
      for (int i = 0; i < resources.size(); i++) {
        jsonResources.set(i, resources.get(i).getJson());
      }
      js.put("resources", jsonResources);
    }
    
    js.put("isOpen", isOpen);
    
    return js.toJson();
  }
  
}
