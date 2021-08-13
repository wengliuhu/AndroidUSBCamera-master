#include $(call all-subdir-makefiles)
#include "PROJ_PATH.h"

PROJ_PATH	:= $(call my-dir)

LOCAL_LDFLAGS += -L$(PROJ_PATH)/so/ -lTxtOverlay
LOCAL_LDFLAGS += -L$(PROJ_PATH)/so/ -lUtils

include $(CLEAR_VARS)
include $(PROJ_PATH)/UVCCamera/Android.mk
include $(PROJ_PATH)/libjpeg-turbo-1.5.0/Android.mk
include $(PROJ_PATH)/libusb/android/jni/Android.mk
include $(PROJ_PATH)/libuvc/android/jni/Android.mk