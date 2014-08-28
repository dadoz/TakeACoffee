CREATE TABLE `takeacoffee`.`coffee_machines` (
  `id` INT NOT NULL,
  `icon_path` VARCHAR(45) NULL,
  `name` VARCHAR(45) NOT NULL,
  `address` VARCHAR(45) NOT NULL,
  `review_list_id` INT NULL,
  PRIMARY KEY (`id`));
