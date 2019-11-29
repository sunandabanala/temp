package com.auzmor.calendar.controllers;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@ApiResponses(value = {
  @ApiResponse(code = 400, message = "Bad Request"),
  @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
  @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
  @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
  @ApiResponse(code = 405, message = "Method Not Allowed"),
  @ApiResponse(code = 412, message = "Precondition Failed"),
  @ApiResponse(code = 415, message = "Unsupported Media Type"),
  @ApiResponse(code = 500, message = "Server Error"),
}
)
public class Controller {
}
