package com.auzmor.calendar.controllers;

import com.auzmor.calendar.controllers.requests.events.EventCreateRequest;
import com.auzmor.calendar.controllers.requests.events.EventUpdateRequest;
import com.auzmor.calendar.controllers.requests.events.SlotCheckRequest;
import com.auzmor.calendar.models.UserAccount;
import com.auzmor.calendar.services.AccountService;
import com.auzmor.calendar.services.CalendarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.auzmor.calendar.constants.SwaggerConstant.TAG_CALENDER_APIS;

@RestController
@Api(value="Job", tags = {TAG_CALENDER_APIS})
public class CalendarController extends Controller {

  @Autowired
  CalendarService calendarService;

  @Autowired
  AccountService accountService;

  @ApiOperation(value = "Create a new Event.")
  @RequestMapping(value = "/event", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
 // @PreAuthorize("@customSecurityService.hasPermission(authentication, '" + PermissionConstant.ADMIN_PERMISSION + "', '" + PermissionConstant.CREATE_CANDIDATE_PERMISSION + "')")
  public ResponseEntity<Object> create(@Valid @RequestBody EventCreateRequest request)
    throws Exception {
    return new ResponseEntity<>(calendarService.saveEvent(request.getEventId(), request.getTitle(), request.getExternalTitle(), request.getStart(), request.getEnd(), request.getGuestEmails(), request.getInviteeIds(), request.getDescription(),
      request.getExternalDescription(), request.getLocation()),
      HttpStatus.CREATED);
  }

  @ApiOperation(value = "Update a new Event.")
  @RequestMapping(value = "/event/{id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  // @PreAuthorize("@customSecurityService.hasPermission(authentication, '" + PermissionConstant.ADMIN_PERMISSION + "', '" + PermissionConstant.CREATE_CANDIDATE_PERMISSION + "')")
  public ResponseEntity<Object> update(@PathVariable("id") final String id,
                                      @RequestBody @Valid EventUpdateRequest request)
    throws Exception {
    return new ResponseEntity<>(calendarService.updateEvent(id, request.getTitle(), request.getExternalTitle(), request.getStart(), request.getEnd(), request.getGuestEmails(), request.getInviteeIds(), request.getDescription(), request.getExternalDescription(),
      request.getLocation()),
      HttpStatus.OK);
  }

  @ApiOperation(value = "Check the slot")
  @RequestMapping(value = "/checkAvailability", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  // @PreAuthorize("@customSecurityService.hasPermission(authentication, '" + PermissionConstant.ADMIN_PERMISSION + "', '" + PermissionConstant.CREATE_CANDIDATE_PERMISSION + "')")
  public ResponseEntity<Object> checkAvailability(@RequestBody @Valid SlotCheckRequest request)
    throws Exception {
    return new ResponseEntity<>(calendarService.checkAvailability(request.getEmail(), request.getStart(), request.getEnd()),
      HttpStatus.CREATED);
  }

  @ApiOperation(value = "Delete a  Event.")
  @RequestMapping(value = "/event/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  // @PreAuthorize("@customSecurityService.hasPermission(authentication, '" + PermissionConstant.ADMIN_PERMISSION + "', '" + PermissionConstant.CREATE_CANDIDATE_PERMISSION + "')")
  public ResponseEntity<Object> delete(@PathVariable("id") final String id)
    throws Exception {
    calendarService.deleteEvent(id);
    return new ResponseEntity<>(new HttpHeaders(), HttpStatus.NO_CONTENT);
  }

}
