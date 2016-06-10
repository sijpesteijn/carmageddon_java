/*
 * DeviceTreeOverlay.h
 *
 *  Created on: Jun 9, 2016
 *      Author: gijs
 */

#ifndef OBJECTS_DEVICETREE_H_
#define OBJECTS_DEVICETREE_H_

struct overlay {
	int id;
	const char *board_name;
	const char *part_number;
	const char *version;
	const char *manufacturer;
	const char *file_name;
};

class DeviceTreeOverlay
{
public:
	int load_device_tree_overlay(struct overlay* ol);
	int unload_device_tree_overlay(int slot_nr);

private:
	int get_device_tree_overlay_count();
	int get_device_tree_overlays(struct overlay** overlays);
	int device_tree_overlay_equal(struct overlay* ol1, struct overlay* ol2);
	int is_device_tree_overlay_loaded(struct overlay* ol);
};

#endif /* OBJECTS_DEVICETREE_H_ */
