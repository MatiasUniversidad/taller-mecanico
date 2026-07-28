import { handleResponse } from './apiUtils';

const BASE = '/api/clientes';

export const getClientes = () =>
  fetch(BASE).then(handleResponse);

export const getCliente = (id) =>
  fetch(`${BASE}/${id}`).then(handleResponse);

export const createCliente = (data) =>
  fetch(BASE, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  }).then(handleResponse);

export const updateCliente = (id, data) =>
  fetch(`${BASE}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  }).then(handleResponse);

export async function deleteCliente(id) {
  const response = await fetch(`${BASE}/${id}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    let mensaje =
      'No fue posible eliminar el cliente.';

    try {
      const data = await response.json();

      mensaje =
        data.message ||
        data.error ||
        mensaje;
    } catch {
      // La respuesta no contiene JSON.
    }

    throw new Error(mensaje);
  }

  return true;
}

export const getVehiculosByCliente = (
  clienteId
) =>
  fetch(
    `${BASE}/${clienteId}/vehiculos`
  ).then(handleResponse);

export const createVehiculoForCliente = (
  clienteId,
  data
) =>
  fetch(
    `${BASE}/${clienteId}/vehiculos`,
    {
      method: 'POST',
      headers: {
        'Content-Type':
          'application/json',
      },
      body: JSON.stringify(data),
    }
  ).then(handleResponse);