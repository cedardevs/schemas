package org.cedar.schemas.analyze;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.operation.valid.IsValidOp;
import org.locationtech.jts.operation.valid.TopologyValidationError;
import org.cedar.schemas.avro.psi.Discovery;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ValidateGeometry {
  private static final Logger log = LoggerFactory.getLogger(ValidateGeometry.class);

  public static Result validateGeometry(Discovery metadata) {
    if (metadata == null || metadata.getSpatialBounding() == null) {
      return new Result(false, false, "Missing geographic bounding box");
    }

    // The avro objects will deserialize to JSON on toString()
    // In particular spatialBounding will be in a valid GeoJSON format
    String geoJson = metadata.getSpatialBounding().toString();
    GeoJsonReader reader = new GeoJsonReader();

    try {
      Geometry geometry = reader.read(geoJson);
      IsValidOp validOp = new IsValidOp(geometry);
      TopologyValidationError err = validOp.getValidationError();

      boolean isValid = err == null;
      String errorMsg = isValid ? null : err.getMessage();

      return new Result(true, isValid, errorMsg);
    }
    catch (ParseException e) {
      // This error parsing is done since JTS is masking their own detailed error messages and all
      // RuntimeExceptions in a generic "can't parse this" way. We're also returning a more specific message
      // when ClassCastExceptions are thrown, instead of the default java message given by JTS
      String message;
      if (e.getCause() instanceof IllegalArgumentException) {
        message = e.getCause().getMessage();
      }
      else if (e.getCause() instanceof ClassCastException) {
        message = "Non-numeric Coordinate";
      }
      else {
        // Just default to the generic JTS message. It's plausible this is unreachable code looking at our use
        // of GeoJsonReader & the source code (https://github.com/locationtech/jts/blob/1.16.x/modules/io/common/src/main/java/org/locationtech/jts/io/geojson/GeoJsonReader.java)
        message = e.getMessage();
      }

      return new Result(true, false, message);
    }
  }

  public static class Result {
    public final boolean exists;
    public final boolean isValid;
    public final String error;

    Result(boolean exists, boolean isValid, String error) {
      this.exists = exists;
      this.isValid = isValid;
      this.error = error;
    }
  }
}
