#Convert raw surface data into tables

import os
from time import sleep

DATA_FOLDER = "../../ftp.ncdc.noaa.gov/pub/data/noaa"

def main():
  for sdir, dirs, files in os.walk(DATA_FOLDER):
    for file in files:
      print("Processing {}".format(file))
      print("Unzipping {}".format(file))
      os.system('gunzip ' + os.path.join(sdir, file))
      print("Formatting {} into table".format(file))
      os.system('java ishJava {} {}'.format(file,file+'.txt'))
      sleep(1)

if __name__=='__main__':
  print("I'm main!")
  main()
