<?php

ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);

require_once 'Products.php';

$Products = new Products;

/*
$allProducts = $Products->getAllProducts();
$i=1;
foreach ($allProducts as $key => $product) {
  printf ("%s     :     %s \n", $i, $product->getOfferId());
  $i++;
}
*/

print_r($Products->csvAvailabilities);
$productsFMC = $Products->getAllProducts(); // Products from Merchant Center
$productsToUpdateSP = [];
$productsToUpdateP = [];
foreach ($productsFMC as $key => $product) {
    $offerId = $product->getOfferId();
    if(isset($Products->csvPrices[$offerId])){
        $priceValue = $Products->csvPrices[$offerId];
        if($product->getSalePrice()){
            if($priceValue != $product->getSalePrice()->getValue()){
                $price = new Google_Service_ShoppingContent_Price();
                $price->setValue($priceValue);
                $price->setCurrency($product->getSalePrice()->getCurrency());
                $product->setSalePrice($price);
                unset($product->source);
                $productsToUpdateSP[] = $product;
            }
        }else{
            if($priceValue != $product->getPrice()->getValue()){
                $price = new Google_Service_ShoppingContent_Price();
                $price->setValue($priceValue);
                $price->setCurrency($product->getPrice()->getCurrency());
                $product->setPrice($price);
                unset($product->source);
                $productsToUpdateP[] = $product;
            }
        }
    }
}


$i=1;
printf ("----------------- Products to update SP: -----------------\n");
foreach($productsToUpdateSP as $productToUpdate){
    printf ("%s : %s : %s \n", $i, $productToUpdate->getOfferId(), $productToUpdate->getSalePrice()->getValue());
    $i++;
}
printf ("----------------- Products to update P: -----------------\n");
foreach($productsToUpdateP as $productToUpdate){
    printf ("%s : %s : %s \n", $i, $productToUpdate->getOfferId(), $productToUpdate->getPrice()->getValue());
    $i++;
}

$Products->insertProductBatch($productsToUpdateP);
$Products->insertProductBatch($productsToUpdateSP);
/*
$product = $Products->getProduct(27103, 'de', 'AT');
print_r($product);
$product->setId($Products->buildProductId(27103, 'de', 'DE'));
$product->setTargetCountry('DE');
$product->getShipping()[0]->setCountry('DE');
$product->setLink(str_replace('86702', '86706', $product->getLink()));
unset($product->source);
print_r($Products->updateProduct($product));


$product = $Products->getProduct(92210, 'de', 'AT');
print_r($product);
$product->setId($Products->buildProductId(92210, 'de', 'DE'));
$product->setTargetCountry('DE');
$product->getShipping()[0]->setCountry('DE');
$product->setLink(str_replace('86702', '86706', $product->getLink()));
unset($product->source);
print_r($Products->updateProduct($product));
*/

?>
