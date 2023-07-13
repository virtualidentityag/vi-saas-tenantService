package com.vi.tenantservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/** Starter class for the application. */
@SpringBootApplication
@EnableSwagger2
public class TenantServiceApplication {

  /**
   * Global application entry point.
   *
   * @param args possible provided args
   */
  public static void main(String[] args) {
    SpringApplication.run(TenantServiceApplication.class, args);
  }
}
