# K3s Master Node (Vezérlő gép)
resource "google_compute_instance" "k3s_master" {
  name         = "k3s-master-node"
  machine_type = "e2-medium"

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2204-lts"
      size  = 30
    }
  }

  network_interface {
    network = "default"
    access_config {
      # Publikus (Külső) IP címet ad a gépnek
    }
  }

  tags = ["k8s-node", "http-server", "https-server"]
}

# K3s Worker Node (Munkagép)
resource "google_compute_instance" "k3s_worker" {
  name         = "k3s-worker-node"
  machine_type = "e2-medium"

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2204-lts"
      size  = 30
    }
  }

  network_interface {
    network = "default"
    access_config {
      # Publikus IP a workernek is
    }
  }

  tags = ["k8s-node"]
}

# Kiírjuk a terminálba az elkészült gépek IP címeit
output "master_public_ip" {
  value = google_compute_instance.k3s_master.network_interface[0].access_config[0].nat_ip
}

output "worker_public_ip" {
  value = google_compute_instance.k3s_worker.network_interface[0].access_config[0].nat_ip
}

# Tűzfal: Web és SSH engedélyezése (Bárhonnan)
resource "google_compute_firewall" "allow_web_ssh" {
  name    = "allow-web-and-ssh"
  network = "default"

  allow {
    protocol = "tcp"
    ports    = ["22", "80", "443"] # 22: SSH (kezelés), 80/443: Web (a klienseknek)
  }

  source_ranges = ["0.0.0.0/0"] # Bárhonnan
  target_tags   = ["http-server", "https-server", "k8s-node"]
}

# Tűzfal: Kubernetes/K3s belső kommunikáció
resource "google_compute_firewall" "allow_k3s_internal" {
  name    = "allow-k3s-internal"
  network = "default"

  # Ezek a portok a K3s "idegrendszere".
  # A 6443 a fővezérlő (API Server), a többi a podok közötti beszélgetéshez kell.
  allow {
    protocol = "tcp"
    ports    = ["6443", "10250", "2379", "2380", "8472"]
  }
  allow {
    protocol = "udp"
    ports    = ["8472"]
  }

  # Ez csak a VPC (belső hálózat) tartományból engedélyezett
  source_ranges = ["10.128.0.0/9"]
  target_tags   = ["k8s-node"]
}

# DNS Zóna létrehozása a Google Cloudban
resource "google_dns_managed_zone" "food_ordering_zone" {
  name        = "food-ordering-zone"
  dns_name    = "ors-food-ordering.com."
  description = "DNS zone for Food Ordering System"
}

# A fő belépési pont (API Gateway)
resource "google_dns_record_set" "gateway_record" {
  name         = "food-order.${google_dns_managed_zone.food_ordering_zone.dns_name}"
  managed_zone = google_dns_managed_zone.food_ordering_zone.name
  type         = "A"
  ttl          = 300

  rrdatas      = [google_compute_instance.k3s_master.network_interface[0].access_config[0].nat_ip]
}

# 'CNAME' rekord: A Menu Service aldomainje
resource "google_dns_record_set" "menu_record" {
  name         = "menu.food-order.${google_dns_managed_zone.food_ordering_zone.dns_name}"
  managed_zone = google_dns_managed_zone.food_ordering_zone.name
  type         = "CNAME"
  ttl          = 300

  # Ez a fenti 'A' rekordra mutat
  rrdatas      = [google_dns_record_set.gateway_record.name]
}