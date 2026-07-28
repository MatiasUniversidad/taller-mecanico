import { handleResponse } from './apiUtils';

const BASE = '/api/reservas';

export const getReservas = () =>
  fetch(BASE).then(handleResponse);

export const createReserva = (data) =>
  fetch(BASE, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  }).then(handleResponse);

export async function deleteReserva(id) {
  const response = await fetch(`${BASE}/${id}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    let mensaje =
      'No fue posible eliminar la reserva.';

    try {
      const data = await response.json();
      mensaje =
        data.message ||
        data.error ||
        mensaje;
    } catch {
      // La respuesta no contenía JSON.
    }

    throw new Error(mensaje);
  }

  return true;
}