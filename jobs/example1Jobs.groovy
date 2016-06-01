freeStyleJob('test-job') {
  properties{
    promotions {
      promotion {
        name('Development')
        conditions {
          manual('testuser')
        }
        actions {
          shell('echo hello;')
        }
      }
    }
  }
}