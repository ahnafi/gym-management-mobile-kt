package com.triosalak.gymmanagement.utils

object ImageUtils {
    
    /**
     * Parse images string from Laravel API response
     * Input example: "['membership_package/mp1', 'membership_package/mp2']"
     * Output: List of image URLs
     */
    fun parseImagesString(imagesString: String?, baseUrl: String = Constants.STORAGE_URL): List<String> {
        if (imagesString.isNullOrEmpty()) return emptyList()
        
        return try {
            // Remove brackets and quotes, then split by comma
            val cleanString = imagesString
                .replace("[", "")
                .replace("]", "")
                .replace("'", "")
                .replace("\"", "")
                .trim()
            
            if (cleanString.isEmpty()) return emptyList()
            
            cleanString.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { imagePath ->
                    // Construct full URL
                    if (imagePath.startsWith("http")) {
                        imagePath
                    } else {
                        "$baseUrl$imagePath"
                    }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get the first image URL from the parsed list
     */
    fun getFirstImageUrl(imagesString: String?, baseUrl: String = Constants.STORAGE_URL): String? {
        val imageList = parseImagesString(imagesString, baseUrl)
        return imageList.firstOrNull()
    }
}