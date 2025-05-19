package org.vaadin.addons.componentfactory.schedulexcalendar.model;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.vaadin.addons.componentfactory.schedulexcalendar.model.Calendar.ColorDefinition;

@SuppressWarnings("serial")
public class Resource implements Serializable {

  private String label;
  
  private String labelHtml;
  
  private String id;
    
  private String colorName; 
  
  private ColorDefinition lightColors;
  
  private ColorDefinition darkColors;
  
  private List<Resource> resources = new ArrayList<Resource>();
  
  private boolean isOpen = true;
  
  /**
   * Constructs an {@code Resource} with the specified id.
   * 
   * @param id the id of the resource
   */
  public Resource(String id) {
    this.id = id;
  }
  
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getLabelHtml() {
    return labelHtml;
  }

  public void setLabelHtml(String labelHtml) {
    this.labelHtml = labelHtml;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getColorName() {
    return colorName;
  }

  public void setColorName(String colorName) {
    this.colorName = colorName;
  }

  public ColorDefinition getLightColors() {
    return lightColors;
  }

  public void setLightColors(ColorDefinition lightColors) {
    this.lightColors = lightColors;
  }

  public ColorDefinition getDarkColors() {
    return darkColors;
  }

  public void setDarkColors(ColorDefinition darkColors) {
    this.darkColors = darkColors;
  }
  
  public List<Resource> getResources() {
    return resources;
  }

  public void setResources(List<Resource> resources) {
    this.resources = resources;
  }

  public boolean isOpen() {
    return isOpen;
  }

  public void setOpen(boolean isOpen) {
    this.isOpen = isOpen;
  }

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
    Optional.ofNullable(colorName).ifPresent(value -> js.put("colorName", value));
    Optional.ofNullable(lightColors).ifPresent(colors -> js.put("lightColors", colors.toJsonObject()));
    Optional.ofNullable(darkColors).ifPresent(colors -> js.put("darkColors", colors.toJsonObject()));
    
    if (resources != null && !resources.isEmpty()) {
      JsonArray resArray = Json.createArray();
      for (int i = 0; i < resources.size(); i++) {
        resArray.set(i, Json.parse(resources.get(i).getJson()));
      }
      js.put("resources", resArray);
    }
    
    js.put("isOpen", isOpen);
    
    return js.toJson();
  }
  
}
