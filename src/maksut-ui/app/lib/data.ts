import { backendUrl } from "@/app/lib/configurations";
import { Lasku } from "@/app/lib/types";

export const fetchLaskutBySecret = async (secret: string | undefined): Promise<Array<Lasku>> => {
  if (secret === 'astu') {
    return [{
      "order_id": "TTU123456-1",
      "first_name": "Etunimi",
      "last_name": "Sukunimi",
      "amount": "114.00",
      "due_date": "2025-03-06",
      "status": "active",
      "secret": "VFRVMTIzNDU2LTEBFby5E6uiWGwIPya18-yl1IZ43MGt6PDOcGcqZbA9xyYVw",
      "paid_at": "",
      "origin": "astu",
      reference: "astu lomake",
    }]
  }
  else if (secret === 'tutu') {
    return [
      {
        "order_id": "TTU123456-1",
        "first_name": "Etunimi",
        "last_name": "Sukunimi",
        "amount": "114.00",
        "due_date": "2025-03-06",
        "status": "active",
        "secret": "VFRVMTIzNDU2LTEBFby5E6uiWGwIPya18-yl1IZ43MGt6PDOcGcqZbA9xyYVw",
        "paid_at": "",
        "origin": "tutu",
        reference: "tutu lomake",
      },
      {
        "order_id": "TTU123456-1",
        "first_name": "Etunimi",
        "last_name": "Sukunimi",
        "amount": "114.00",
        "due_date": "2025-03-06",
        "status": "active",
        "secret": "VFRVMTIzNDU2LTEBFby5E6uiWGwIPya18-yl1IZ43MGt6PDOcGcqZbA9xyYVw",
        "paid_at": "",
        "origin": "tutu",
        reference: "tutu lomake",
      }]
  }
  throw Error()
  // if (secret) {
  //   const response = await fetch(`${backendUrl}/laskut-by-secret?secret=${secret}`)
  //   return await response.json() as Promise<[Lasku]>
  // } else {
  //   throw Error('No secret')
  // }
}