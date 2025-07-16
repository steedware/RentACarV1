-- Update any vehicles with unknown types to a known type
UPDATE vehicle SET type = 'SEDAN' 
WHERE type NOT IN ('SEDAN', 'SUV', 'HATCHBACK', 'COMPACT', 'ECONOMY');
