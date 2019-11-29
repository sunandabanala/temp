package com.auzmor.calendar.controllers;

import com.auzmor.calendar.services.CalendarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.auzmor.calendar.constants.SwaggerConstant.TAG_CALENDER_APIS;

@RestController
@Api(value="Job", tags = {TAG_CALENDER_APIS})
public class CalendarController extends Controller {

  @Autowired
  CalendarService calendarService;

  @ApiOperation(value = "Create a new Event.",  response = List.class)
  @RequestMapping(value = "/candidate", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
 // @PreAuthorize("@customSecurityService.hasPermission(authentication, '" + PermissionConstant.ADMIN_PERMISSION + "', '" + PermissionConstant.CREATE_CANDIDATE_PERMISSION + "')")
  public ResponseEntity<List> create()
    throws Exception {
    return new ResponseEntity<List>(calendarService.create(),
      HttpStatus.CREATED);
  }
}