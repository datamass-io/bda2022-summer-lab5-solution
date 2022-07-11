# create search service in Basic SKU (stock keeping unit)
az search service create -n mdsearch4321 -g BigDataAcademyJuly2022 -l uksouth --sku Standard
# show admin keys
az search admin-key show -g BigDataAcademyJuly2022 --service-name mdsearch4321
