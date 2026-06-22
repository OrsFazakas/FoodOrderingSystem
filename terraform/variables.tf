variable "project_id" {
  description = "A Google Cloud Project ID-ja"
  type        = string
  default     = "foodordering-microservices"
}

variable "region" {
  description = "A GCP régió"
  type        = string
  default     = "europe-west3"
}

variable "zone" {
  description = "A GCP zóna"
  type        = string
  default     = "europe-west3-c"
}